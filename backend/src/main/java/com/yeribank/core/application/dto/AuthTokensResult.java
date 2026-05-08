package com.yeribank.core.application.dto;

public record AuthTokensResult(
    String accessToken,
    String refreshToken,
    String tokenType,
    long expiresInSeconds) {}
