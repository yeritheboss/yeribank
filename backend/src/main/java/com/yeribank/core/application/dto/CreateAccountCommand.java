package com.yeribank.core.application.dto;

import java.math.BigDecimal;
import java.util.UUID;

public record CreateAccountCommand(
    UUID ownerUserId, BigDecimal initialBalance, UUID actorUserId, boolean actorAdmin) {}
