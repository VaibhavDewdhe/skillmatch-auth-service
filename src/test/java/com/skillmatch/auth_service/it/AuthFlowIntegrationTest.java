package com.skillmatch.auth_service.it;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.skillmatch.auth_service.dto.LoginRequest;
import com.skillmatch.auth_service.dto.RegisterRequest;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.*;
import org.springframework.web.client.RestTemplate;

import java.net.URI;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class AuthFlowIntegrationTest extends ContainersBase {

    @LocalServerPort int port;
    @Autowired ObjectMapper om;

    private String baseUrl(String path) {
        return "http://localhost:" + port + path;
    }

    @Test
    void registerLoginAndAccessSecured() throws Exception {
        var rt = new RestTemplate();

        // 1) Register
        RegisterRequest reg = new RegisterRequest();
        reg.setFullName("Vaibhav D");
        reg.setEmail("vaibhav@example.com");
        reg.setPassword("secret123");

        var regResp = rt.postForEntity(
                URI.create(baseUrl("/api/auth/register")),
                new HttpEntity<>(reg),
                String.class);

        assertEquals(HttpStatus.OK, regResp.getStatusCode());

        // 2) Login
        LoginRequest login = new LoginRequest();
        login.setEmail("vaibhav@example.com");
        login.setPassword("secret123");

        var loginResp = rt.postForEntity(
                URI.create(baseUrl("/api/auth/login")),
                new HttpEntity<>(login),
                String.class);

        assertEquals(HttpStatus.OK, loginResp.getStatusCode());

        JsonNode node = om.readTree(loginResp.getBody());
        String token = node.get("token").asText();
        assertNotNull(token);

        // 3) Access secured endpoint
        var headers = new HttpHeaders();
        headers.setBearerAuth(token);
        var resp = rt.exchange(
                URI.create(baseUrl("/test/secure")),
                HttpMethod.GET,
                new HttpEntity<>(headers),
                String.class);

        assertEquals(HttpStatus.OK, resp.getStatusCode());
        assertEquals("secure-ok", resp.getBody());
    }
}
