package com.yeribank.core.application.dto;

import java.util.UUID;

public record GetRiskProfileQuery(UUID requestedUserId, UUID actorUserId, boolean actorAdmin) {}
