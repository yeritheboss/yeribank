package com.yeribank.core.application.dto;

import com.yeribank.core.domain.model.enums.FraudAlertStatus;
import java.util.UUID;

public record ListFraudAlertsQuery(FraudAlertStatus status, UUID actorUserId, boolean actorAdmin) {}
