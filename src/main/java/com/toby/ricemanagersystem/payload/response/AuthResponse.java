package com.toby.ricemanagersystem.payload.response;

import lombok.Data;

@Data
public class AuthResponse {
    private String token;
    private String type = "Bearer";
    private Long id;
    private String username;
    private String role;
    private final String status;

    public AuthResponse(String accessToken, Long id, String username, String role, String status) {
        this.token = accessToken;
        this.id = id;
        this.username = username;
        this.role = role;
        this.status = status;
    }
}
