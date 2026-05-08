package com.yeribank.core.infrastructure.web.dto;

import com.yeribank.core.domain.model.enums.FraudAlertStatus;
import jakarta.validation.constraints.NotNull;

public record ReviewFraudAlertRequest(@NotNull FraudAlertStatus status) {}
