package com.skillmatch.auth_service.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.skillmatch.auth_service.dto.LoginRequest;
import com.skillmatch.auth_service.dto.LoginResponse;
import com.skillmatch.auth_service.dto.RegisterRequest;
import com.skillmatch.auth_service.dto.RegisterResponse;
import com.skillmatch.auth_service.service.AuthService;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(AuthController.class)
class AuthControllerTest {

    @Autowired MockMvc mvc;
    @Autowired ObjectMapper om;

    @MockBean AuthService authService;

    @Test
    void register_valid() throws Exception {
        RegisterRequest req = new RegisterRequest();
        req.setFullName("Vaibhav");
        req.setEmail("v@example.com");
        req.setPassword("secret123");

        Mockito.when(authService.registerUser(any()))
                .thenReturn(new RegisterResponse("User registered successfully!", "v@example.com"));

        mvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(om.writeValueAsString(req)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.email").value("v@example.com"));
    }

    @Test
    void login_valid() throws Exception {
        LoginRequest req = new LoginRequest();
        req.setEmail("v@example.com");
        req.setPassword("secret123");

        Mockito.when(authService.loginUser(any()))
                .thenReturn(new LoginResponse("Login successful!", "jwt-token", "Vaibhav", List.of("LEARN","TEACH")));

        mvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(om.writeValueAsString(req)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token").value("jwt-token"));
    }

    @Test
    void register_validationErrors() throws Exception {
        RegisterRequest bad = new RegisterRequest(); // empty -> should fail @Valid
        mvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(om.writeValueAsString(bad)))
                .andExpect(status().isBadRequest());
    }
}
