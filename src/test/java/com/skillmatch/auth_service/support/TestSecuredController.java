package com.skillmatch.auth_service.support;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class TestSecuredController {

    @GetMapping("/test/secure")
    public String secureEcho() {
        return "secure-ok";
    }
}
