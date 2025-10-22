package com.skillmatch.auth_service.service;

import com.skillmatch.auth_service.dto.LoginRequest;
import com.skillmatch.auth_service.dto.RegisterRequest;
import com.skillmatch.auth_service.model.User;
import com.skillmatch.auth_service.repository.UserRepository;
import com.skillmatch.auth_service.util.JwtUtil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class AuthServiceTest {

    private UserRepository userRepository;
    private PasswordEncoder passwordEncoder;
    private JwtUtil jwtUtil;
    private AuthService authService;

    @BeforeEach
    void setUp() {
        userRepository = mock(UserRepository.class);
        passwordEncoder = mock(PasswordEncoder.class);
        jwtUtil = mock(JwtUtil.class);
        authService = new AuthService(userRepository, passwordEncoder, jwtUtil);
    }

    @Test
    void registerUser_success() {
        RegisterRequest req = new RegisterRequest();
        req.setFullName("Vaibhav");
        req.setEmail("v@example.com");
        req.setPassword("secret123");

        when(userRepository.findByEmail("v@example.com")).thenReturn(Optional.empty());
        when(passwordEncoder.encode("secret123")).thenReturn("ENC");

        var resp = authService.registerUser(req);
        assertEquals("User registered successfully!", resp.getMessage());
        assertEquals("v@example.com", resp.getEmail());

        ArgumentCaptor<User> captor = ArgumentCaptor.forClass(User.class);
        verify(userRepository).save(captor.capture());
        assertEquals("v@example.com", captor.getValue().getEmail());
        assertEquals("ENC", captor.getValue().getPassword());
        assertTrue(captor.getValue().isEnabled());
    }

    @Test
    void registerUser_duplicateEmail() {
        RegisterRequest req = new RegisterRequest();
        req.setFullName("Vaibhav");
        req.setEmail("v@example.com");
        req.setPassword("secret123");

        when(userRepository.findByEmail("v@example.com")).thenReturn(Optional.of(new User()));
        assertThrows(IllegalArgumentException.class, () -> authService.registerUser(req));
        verify(userRepository, never()).save(any());
    }

    @Test
    void loginUser_success() {
        LoginRequest req = new LoginRequest();
        req.setEmail("v@example.com");
        req.setPassword("secret123");

        User u = User.builder().id(1L).email("v@example.com").password("ENC").enabled(true).build();
        when(userRepository.findByEmail("v@example.com")).thenReturn(Optional.of(u));
        when(passwordEncoder.matches("secret123", "ENC")).thenReturn(true);
        when(jwtUtil.generateToken(u)).thenReturn("jwt");

        var resp = authService.loginUser(req);
        assertEquals("Login successful!", resp.getMessage());
        assertEquals("jwt", resp.getToken());
    }

    @Test
    void loginUser_badPassword() {
        LoginRequest req = new LoginRequest();
        req.setEmail("v@example.com");
        req.setPassword("oops");

        User u = User.builder().id(1L).email("v@example.com").password("ENC").enabled(true).build();
        when(userRepository.findByEmail("v@example.com")).thenReturn(Optional.of(u));
        when(passwordEncoder.matches("oops", "ENC")).thenReturn(false);

        assertThrows(IllegalArgumentException.class, () -> authService.loginUser(req));
    }
}
