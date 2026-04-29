package ru.justspeak.service;

import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.justspeak.api.dto.ApiAuthRegisterPost201Response;
import ru.justspeak.api.dto.AuthResponse;
import ru.justspeak.api.dto.UserLoginRequest;
import ru.justspeak.api.dto.UserRegisterRequest;
import ru.justspeak.jooq.tables.records.ProfilesRecord;
import ru.justspeak.jooq.tables.records.UsersRecord;
import ru.justspeak.model.CustomUserDetails;
import ru.justspeak.repository.ProfileRepository;
import ru.justspeak.repository.UserRepository;
import ru.justspeak.service.util.CustomUserDetailsService;
import ru.justspeak.service.util.JwtService;
import ru.justspeak.util.UserMapper;


@Service
@RequiredArgsConstructor
public class AuthService {


    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;
    private final CustomUserDetailsService userDetailsService;
    private final ProfileRepository profileRepository;


    @Transactional
    public ApiAuthRegisterPost201Response register(UserRegisterRequest userRegisterRequest) {
        UsersRecord recordUser = UserMapper.toRecord(userRegisterRequest, passwordEncoder);
        recordUser.setRole("ROLE_USER");
        recordUser.setCreatedAt(java.time.LocalDateTime.now());
        recordUser.setIsVerified(false);
        recordUser.setDeleted(false);
        UsersRecord savedUser = userRepository.save(recordUser);
        ProfilesRecord profilesRecord = profileRepository.create(savedUser, userRegisterRequest);
        return new ApiAuthRegisterPost201Response().message("Success").userId(savedUser.getId());
    }


    public AuthResponse login(UserLoginRequest userLoginRequest) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(userLoginRequest.getUsername(), userLoginRequest.getPassword())
        );
        CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();

        String token = jwtService.generateToken(userDetails,userDetails.getId());

        return new AuthResponse()
                .token(token)
                .userId(userDetails.getId())
                .username(userLoginRequest.getUsername())
                .roles(userDetails.getAuthorities().stream()
                        .map(GrantedAuthority::getAuthority)
                        .toList());

    }






}
