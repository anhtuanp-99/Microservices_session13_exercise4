package com.pharmacy.identityservice.dto.response;

import lombok.*;

import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class LoginResponse {

    private String accessToken;
    private String tokenType;
    private long expiresIn;      // giây
    private UUID userId;
    private String username;
    private String role;
}