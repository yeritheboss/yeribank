package com.yeribank.core.application.dto;

import com.yeribank.core.domain.model.enums.FraudAlertStatus;
import java.util.UUID;

public record ReviewFraudAlertCommand(
    UUID alertId, FraudAlertStatus status, UUID actorUserId, boolean actorAdmin) {}
