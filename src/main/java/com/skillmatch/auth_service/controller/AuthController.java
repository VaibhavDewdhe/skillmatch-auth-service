package com.skillmatch.auth_service.controller;

import com.skillmatch.auth_service.dto.LoginRequest;
import com.skillmatch.auth_service.dto.LoginResponse;
import com.skillmatch.auth_service.dto.RegisterRequest;
import com.skillmatch.auth_service.dto.RegisterResponse;
import com.skillmatch.auth_service.service.UserService;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final UserService userService;

    public AuthController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping("/register")
    public RegisterResponse registerUser(@RequestBody RegisterRequest request) {
        return userService.registerUser(request);
    }

    @PostMapping("/login")
    public LoginResponse loginUser(@RequestBody LoginRequest request) {
        return userService.loginUser(request);
    }

    @GetMapping("/ping")
    public String ping() {
        return "Auth Service is running 🚀";
    }
}
