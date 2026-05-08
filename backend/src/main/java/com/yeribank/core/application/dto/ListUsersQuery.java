package com.yeribank.core.application.dto;

import java.util.UUID;

public record ListUsersQuery(UUID actorUserId, boolean actorAdmin) {}
