package ru.justspeak.controller;


import io.swagger.v3.oas.annotations.security.SecurityRequirements;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.justspeak.api.dto.ApiAuthRegisterPost201Response;
import ru.justspeak.api.dto.AuthResponse;
import ru.justspeak.api.dto.UserLoginRequest;
import ru.justspeak.api.dto.UserRegisterRequest;
import ru.justspeak.service.AuthService;


@RestController
@RequestMapping("api/auth")
public class AuthController{

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }


    @SecurityRequirements
    @PostMapping("/register")
    public ResponseEntity<ApiAuthRegisterPost201Response> register(
            @Valid @RequestBody UserRegisterRequest registerRequest
            ) {
        return ResponseEntity.status(201).body(authService.register(registerRequest));
    }


    @SecurityRequirements
    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@Valid @RequestBody UserLoginRequest loginRequest) {
        return ResponseEntity.ok(authService.login(loginRequest));
    }

}
