package com.skillmatch.auth_service.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class RegisterRequest {

    @NotBlank
    @Size(min = 2, max = 120)
    private String fullName;

    @NotBlank
    @Email
    @Size(max = 191)
    private String email;

    @NotBlank
    @Size(min = 8, max = 72)
    private String password;
}
