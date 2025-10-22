package com.skillmatch.auth_service.dto;

import com.skillmatch.auth_service.model.Role;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class RegisterRequest {
    private String fullName;
    private String email;
    private String password;
    private Role role;
}
