package ru.justspeak.service;

import org.postgresql.util.PSQLException;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import ru.justspeak.api.dto.ApiAuthRegisterPost201Response;
import ru.justspeak.api.dto.AuthResponse;
import ru.justspeak.api.dto.UserLoginRequest;
import ru.justspeak.api.dto.UserRegisterRequest;
import ru.justspeak.exception.DuplicateResourceException;
import ru.justspeak.jooq.tables.records.UsersRecord;
import ru.justspeak.model.CustomUserDetails;
import ru.justspeak.repository.ProfileRepository;
import ru.justspeak.repository.UserRepository;
import ru.justspeak.service.util.JwtService;
import ru.justspeak.util.UserMapper;

@Service
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;
    private final ProfileRepository profileRepository;

    public AuthService(UserRepository userRepository,
                       PasswordEncoder passwordEncoder,
                       AuthenticationManager authenticationManager,
                       JwtService jwtService,
                       ProfileRepository profileRepository) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.authenticationManager = authenticationManager;
        this.jwtService = jwtService;
        this.profileRepository = profileRepository;
    }

    @Transactional
    public ApiAuthRegisterPost201Response register(UserRegisterRequest userRegisterRequest) {
        if (userRepository.findByUsername(userRegisterRequest.getUsername()).isPresent()) {
            throw new DuplicateResourceException("Username '" + userRegisterRequest.getUsername() + "' is already taken");
        }
        if (userRepository.findByEmail(userRegisterRequest.getEmail()).isPresent()) {
            throw new DuplicateResourceException("Email '" + userRegisterRequest.getEmail() + "' is already registered");
        }

        UsersRecord recordUser = UserMapper.toRecord(userRegisterRequest, passwordEncoder);
        UsersRecord savedUser;
        try {
            savedUser = userRepository.save(recordUser);
        } catch (DataIntegrityViolationException e) {
            if (e.getMostSpecificCause() instanceof PSQLException ps && "23505".equals(ps.getSQLState())) {
                throw new DuplicateResourceException("User with this username or email already exists");
            }
            throw e;
        }

        profileRepository.create(savedUser, userRegisterRequest);

        return new ApiAuthRegisterPost201Response()
                .message("User registered successfully")
                .userId(savedUser.getId());
    }

    public AuthResponse login(UserLoginRequest userLoginRequest) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(userLoginRequest.getUsername(), userLoginRequest.getPassword())
        );
        CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();

        String token = jwtService.generateToken(userDetails, userDetails.getId());

        return new AuthResponse()
                .token(token)
                .userId(userDetails.getId())
                .username(userLoginRequest.getUsername())
                .roles(userDetails.getAuthorities().stream()
                        .map(auth -> auth.getAuthority())
                        .toList());
    }
}
