package com.yeribank.core.application.dto;

import com.yeribank.core.domain.model.enums.RiskLevel;
import java.time.LocalDateTime;
import java.util.UUID;

public record RiskProfileResult(
    UUID userId,
    int score,
    int lastAssessmentScore,
    RiskLevel riskLevel,
    long alertCount90d,
    LocalDateTime updatedAt) {}
