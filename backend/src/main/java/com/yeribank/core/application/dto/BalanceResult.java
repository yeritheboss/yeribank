package com.yeribank.core.application.dto;

import java.math.BigDecimal;
import java.util.UUID;

public record BalanceResult(UUID accountId, BigDecimal balance) {}
