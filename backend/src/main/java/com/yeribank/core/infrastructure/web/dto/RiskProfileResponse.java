package com.yeribank.core.infrastructure.web.dto;

import com.yeribank.core.domain.model.enums.RiskLevel;
import java.time.LocalDateTime;
import java.util.UUID;

public record RiskProfileResponse(
    UUID userId,
    int score,
    int lastAssessmentScore,
    RiskLevel riskLevel,
    long alertCount90d,
    LocalDateTime updatedAt) {}
