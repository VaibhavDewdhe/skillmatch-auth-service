package com.skillmatch.auth_service.it;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.skillmatch.auth_service.model.User;
import com.skillmatch.auth_service.repository.UserRepository;
import com.skillmatch.auth_service.util.JwtUtil;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.*;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.client.RestTemplate;

import java.net.URI;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class SecurityIntegrationTest extends ContainersBase {

    @LocalServerPort int port;
    @Autowired UserRepository repo;
    @Autowired PasswordEncoder encoder;
    @Autowired JwtUtil jwtUtil;
    @Autowired ObjectMapper om;

    private String baseUrl(String path) {
        return "http://localhost:" + port + path;
    }

    @Test
    void secureEndpoint_requiresJwt() {
        var rt = new RestTemplate();

        // 1) Without token -> expect 401
        var resp1 = rt.getForEntity(URI.create(baseUrl("/test/secure")), String.class);
        // NOTE: Spring may return 401 via our entry point; if path not found it'd be 404.
        // We added test controller so mapping exists.
        assertEquals(HttpStatus.UNAUTHORIZED, resp1.getStatusCode());

        // 2) With valid token -> expect 200
        User u = repo.save(User.builder()
                .email("v@example.com")
                .password(encoder.encode("secret123"))
                .fullName("Vaibhav")
                .enabled(true)
                .build());

        String token = jwtUtil.generateToken(u);

        var headers = new HttpHeaders();
        headers.setBearerAuth(token);
        var entity = new HttpEntity<>(headers);

        var resp2 = rt.exchange(URI.create(baseUrl("/test/secure")), HttpMethod.GET, entity, String.class);
        assertEquals(HttpStatus.OK, resp2.getStatusCode());
        assertEquals("secure-ok", resp2.getBody());
    }
}
