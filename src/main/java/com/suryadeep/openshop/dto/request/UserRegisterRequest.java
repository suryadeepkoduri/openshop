package com.suryadeep.openshop.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class UserRegisterRequest {
    @NotBlank(message = "Email shouldn't be empty")
    @Email(message = "Email should be valid")
    private String email;

    @NotBlank(message = "Password shouldn't be empty")
    @Size(min = 8,message = "Password must be at least 8 characters long")
    private String password;

    @NotBlank(message = "Username shouldn't be empty")
    private String username;
}