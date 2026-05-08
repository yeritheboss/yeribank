package com.yeribank.core.infrastructure.web.dto;

import java.time.Instant;

public record ErrorResponse(int status, String message, Instant timestamp) {}
