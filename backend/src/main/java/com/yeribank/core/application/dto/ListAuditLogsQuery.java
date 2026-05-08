package com.yeribank.core.application.dto;

import java.util.UUID;

public record ListAuditLogsQuery(UUID actorUserId, boolean actorAdmin, int limit) {}
