package com.skillmatch.auth_service.util;

import com.skillmatch.auth_service.model.Capability;
import com.skillmatch.auth_service.model.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.EnumSet;

import static org.junit.jupiter.api.Assertions.*;

class JwtUtilTest {

    private JwtUtil jwtUtil;

    @BeforeEach
    void init() {
        // match application-test.yml
        jwtUtil = new JwtUtil("super-secret-jwt-key-for-tests-32bytes-minimum!!");
        jwtUtil.initKey();
    }

    @Test
    void generateAndValidateToken_ok() {
        User domainUser = User.builder()
                .id(42L)
                .email("vaibhav@example.com")
                .fullName("Vaibhav D")
                .capabilities(EnumSet.of(Capability.LEARN, Capability.TEACH))
                .password("x")
                .enabled(true)
                .build();

        String token = jwtUtil.generateToken(domainUser);
        assertNotNull(token);

        String subject = jwtUtil.extractUsername(token);
        assertEquals("vaibhav@example.com", subject);

        UserDetails userDetails = org.springframework.security.core.userdetails.User
                .withUsername("vaibhav@example.com")
                .password("x")
                .authorities("CAP_LEARN", "CAP_TEACH")
                .build();

        assertTrue(jwtUtil.validateToken(token, userDetails));
    }
}
