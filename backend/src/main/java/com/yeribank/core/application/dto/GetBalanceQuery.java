package com.yeribank.core.application.dto;

import java.util.UUID;

public record GetBalanceQuery(UUID accountId, UUID actorUserId, boolean actorAdmin) {}
