package com.yeribank.core.application.dto;

import java.util.UUID;

public record ListLoanApplicationsQuery(UUID requestedUserId, UUID actorUserId, boolean actorAdmin) {}
