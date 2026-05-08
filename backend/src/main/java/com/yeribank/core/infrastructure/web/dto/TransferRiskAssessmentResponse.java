package com.yeribank.core.infrastructure.web.dto;

import com.yeribank.core.domain.model.enums.RiskDecision;
import com.yeribank.core.domain.model.enums.RiskLevel;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

public record TransferRiskAssessmentResponse(
    UUID transferId,
    UUID userId,
    int score,
    RiskDecision decision,
    RiskLevel riskLevel,
    List<String> reasons,
    LocalDateTime createdAt) {}
