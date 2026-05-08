package com.yeribank.core.application.dto;

import java.util.UUID;

public record GetFinancialProfileQuery(UUID requestedUserId, UUID actorUserId, boolean actorAdmin) {}
