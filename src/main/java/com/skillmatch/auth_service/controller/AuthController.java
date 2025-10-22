package com.skillmatch.auth_service.controller;

import com.skillmatch.auth_service.dto.LoginRequest;
import com.skillmatch.auth_service.dto.LoginResponse;
import com.skillmatch.auth_service.dto.RegisterRequest;
import com.skillmatch.auth_service.dto.RegisterResponse;
import com.skillmatch.auth_service.service.AuthService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthService authService;
    public AuthController(AuthService authService) { this.authService = authService; }

    @PostMapping("/register")
    public ResponseEntity<RegisterResponse> registerUser(@Valid @RequestBody RegisterRequest request) {
        return ResponseEntity.ok(authService.registerUser(request));
    }

    @PostMapping("/login")
    public ResponseEntity<LoginResponse> loginUser(@Valid @RequestBody LoginRequest request) {
        return ResponseEntity.ok(authService.loginUser(request));
    }

    @GetMapping("/ping")
    public String ping() { return "Auth Service is running 🚀"; }
}
