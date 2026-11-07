package ru.justspeak.util;

import java.io.IOException;
import java.util.List;

import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.util.AntPathMatcher;
import org.springframework.web.filter.OncePerRequestFilter;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import ru.justspeak.security.CustomAuthenticationEntryPoint;
import ru.justspeak.service.util.CustomUserDetailsService;
import ru.justspeak.service.util.JwtService;
import ru.justspeak.service.util.TokenBlacklistService;

@Component
public class JwtFilter extends OncePerRequestFilter {

    private static final List<String> PERMIT_ALL_PATTERNS = List.of(
            "/auth/**",
            "/api/auth/**",
            "/v3/api-docs",
            "/v3/api-docs/**",
            "/swagger-ui/**",
            "/swagger-ui.html",
            "/swagger-resources/**",
            "/webjars/**",
            "/error"
    );

    private final JwtService jwtService;
    private final CustomUserDetailsService userDetailsService;
    private final CustomAuthenticationEntryPoint authenticationEntryPoint;
    private final TokenBlacklistService tokenBlacklistService;
    private final AntPathMatcher pathMatcher = new AntPathMatcher();

    public JwtFilter(JwtService jwtService,
                     CustomUserDetailsService userDetailsService,
                     CustomAuthenticationEntryPoint authenticationEntryPoint,
                     TokenBlacklistService tokenBlacklistService) {
        this.jwtService = jwtService;
        this.userDetailsService = userDetailsService;
        this.authenticationEntryPoint = authenticationEntryPoint;
        this.tokenBlacklistService = tokenBlacklistService;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {
        final String authorizationHeader = request.getHeader("Authorization");
        if (authorizationHeader == null || !authorizationHeader.startsWith("Bearer ")) {
            filterChain.doFilter(request, response);
            return;
        }

        final String jwt = authorizationHeader.substring(7);

        if (tokenBlacklistService.isBlacklisted(jwt)) {
            handleAuthFailure(request, response, filterChain, "Token has been revoked");
            return;
        }

        try {
            Claims claims = jwtService.extractAllClaims(jwt);
            String userEmail = claims.getSubject();
            if (userEmail != null && SecurityContextHolder.getContext().getAuthentication() == null) {
                UserDetails userDetails = this.userDetailsService.loadUserByUsername(userEmail);

                if (jwtService.isClaimsValid(claims, userDetails) && userDetails.isEnabled() && userDetails.isAccountNonLocked()) {
                    UsernamePasswordAuthenticationToken authToken =
                            new UsernamePasswordAuthenticationToken(
                                    userDetails,
                                    null,
                                    userDetails.getAuthorities()
                            );
                    authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                    SecurityContextHolder.getContext().setAuthentication(authToken);
                } else {
                    handleAuthFailure(request, response, filterChain, "Invalid token or inactive user account");
                    return;
                }
            }
        } catch (JwtException | IllegalArgumentException | AuthenticationException e) {
            handleAuthFailure(request, response, filterChain, "Invalid or expired JWT token");
            return;
        }

        filterChain.doFilter(request, response);
    }

    private void handleAuthFailure(HttpServletRequest request,
                                   HttpServletResponse response,
                                   FilterChain filterChain,
                                   String message) throws ServletException, IOException {
        SecurityContextHolder.clearContext();
        if (isPermitAllRequest(request)) {
            filterChain.doFilter(request, response);
        } else {
            authenticationEntryPoint.commence(request, response, new BadCredentialsException(message));
        }
    }

    private boolean isPermitAllRequest(HttpServletRequest request) {
        String path = request.getRequestURI();
        for (String pattern : PERMIT_ALL_PATTERNS) {
            if (pathMatcher.match(pattern, path)) {
                return true;
            }
        }
        return false;
    }
}
