package com.yeribank.core.application.dto;

import java.util.UUID;

public record ListAccountTransfersQuery(
    String accountNumber, UUID actorUserId, boolean actorAdmin, int limit) {}
