package com.toby.ricemanagersystem.payload.request;

import lombok.Data;

@Data
public class LoginRequest {
    private String username;
    private String password;
}
