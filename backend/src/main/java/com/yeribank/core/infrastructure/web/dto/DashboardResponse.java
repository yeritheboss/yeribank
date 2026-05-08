package com.yeribank.core.infrastructure.web.dto;

import java.util.List;

public record DashboardResponse(
    UserResponse user,
    List<AccountResponse> accounts,
    List<AccountTransferResponse> recentTransfers) {}
