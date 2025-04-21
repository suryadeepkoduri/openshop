package com.suryadeep.openshop.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class LoginRequest {
    @NotBlank(message = "Email shouldn't be empty")
    @Email(message = "Email should be valid")
    private String email;

    @NotBlank(message = "Password shouldn't be empty")
    private String password;
}