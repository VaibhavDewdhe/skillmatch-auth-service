package com.skillmatch.auth_service.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter @Setter
@AllArgsConstructor
public class LoginResponse {
    private String message;
    private String token;
    private String name;
    private List<String> caps; // ["LEARN","TEACH", ...]
}
