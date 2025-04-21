package com.suryadeep.openshop.dto.response;

import lombok.Data;


@Data
public class LoginResponse {
    private String tokenType = "Bearer";
    private String accessToken;

    public LoginResponse(String token) {
        this.accessToken = token;
    }
}