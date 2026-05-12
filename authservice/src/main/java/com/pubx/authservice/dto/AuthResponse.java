package com.pubx.authservice.dto;

import com.pubx.authservice.enums.Role;
import lombok.Builder;
import lombok.Data;


@Data
@Builder
public class AuthResponse {
    private String accessToken;
    private String refreshToken;
    private String tokenType;
    private String userId;
    private String email;
    private Role role;
    private String message;
}