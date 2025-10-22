package com.skillmatch.auth_service.service;

import com.skillmatch.auth_service.model.Capability;
import com.skillmatch.auth_service.model.User;
import com.skillmatch.auth_service.repository.UserRepository;
import org.junit.jupiter.api.Test;

import java.util.EnumSet;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class CustomUserDetailsServiceTest {

    @Test
    void loadUserByUsername_ok() {
        UserRepository repo = mock(UserRepository.class);
        var svc = new CustomUserDetailsService(repo);

        User u = User.builder()
                .email("v@example.com")
                .password("ENC")
                .enabled(true)
                .capabilities(EnumSet.of(Capability.LEARN, Capability.TEACH))
                .build();

        when(repo.findByEmail("v@example.com")).thenReturn(Optional.of(u));

        var ud = svc.loadUserByUsername("v@example.com");
        assertEquals("v@example.com", ud.getUsername());
        assertTrue(ud.getAuthorities().stream().anyMatch(a -> a.getAuthority().equals("CAP_LEARN")));
    }
}
