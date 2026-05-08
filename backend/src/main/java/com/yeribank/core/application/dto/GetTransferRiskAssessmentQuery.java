package com.yeribank.core.application.dto;

import java.util.UUID;

public record GetTransferRiskAssessmentQuery(UUID transferId, UUID actorUserId, boolean actorAdmin) {}
