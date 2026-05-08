package com.yeribank.core.application.dto;

import java.util.UUID;

public record ListAccountsQuery(UUID userId, UUID actorUserId, boolean actorAdmin) {}
