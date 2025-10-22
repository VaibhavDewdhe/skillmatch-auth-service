package com.skillmatch.auth_service.service;

import com.skillmatch.auth_service.dto.LoginRequest;
import com.skillmatch.auth_service.dto.LoginResponse;
import com.skillmatch.auth_service.dto.RegisterRequest;
import com.skillmatch.auth_service.dto.RegisterResponse;
import com.skillmatch.auth_service.model.Capability;
import com.skillmatch.auth_service.model.User;
import com.skillmatch.auth_service.repository.UserRepository;
import com.skillmatch.auth_service.util.JwtUtil;
import jakarta.transaction.Transactional;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.EnumSet;
import java.util.List;

@Service
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;

    public AuthService(UserRepository userRepository, PasswordEncoder passwordEncoder, JwtUtil jwtUtil) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtUtil = jwtUtil;
    }

    @Transactional
    public RegisterResponse registerUser(RegisterRequest request) {
        userRepository.findByEmail(request.getEmail()).ifPresent(u -> {
            throw new IllegalArgumentException("Email already exists");
        });

        User user = User.builder()
                .fullName(request.getFullName())
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .capabilities(EnumSet.of(Capability.LEARN, Capability.TEACH)) // default
                .enabled(true)
                .build();

        userRepository.save(user);
        return new RegisterResponse("User registered successfully!", user.getEmail());
    }

    public LoginResponse loginUser(LoginRequest request) {
        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new IllegalArgumentException("Invalid email or password"));

        if (!user.isEnabled() || !passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            throw new IllegalArgumentException("Invalid email or password");
        }

        String token = jwtUtil.generateToken(user);
        List<String> caps = user.getCapabilities().stream().map(Enum::name).toList();

        return new LoginResponse("Login successful!", token, user.getFullName(), caps);
    }
}
