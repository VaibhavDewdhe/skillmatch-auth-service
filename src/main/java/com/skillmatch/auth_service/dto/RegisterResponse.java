package com.skillmatch.auth_service.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter @Setter
@AllArgsConstructor
public class RegisterResponse {
    private String message;
    private String email;
}
