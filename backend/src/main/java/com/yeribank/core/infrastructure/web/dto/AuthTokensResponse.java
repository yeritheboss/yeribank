package com.yeribank.core.infrastructure.web.dto;

public record AuthTokensResponse(
    String accessToken,
    String refreshToken,
    String tokenType,
    long expiresInSeconds) {}
