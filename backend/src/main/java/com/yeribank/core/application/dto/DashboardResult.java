package com.yeribank.core.application.dto;

import java.util.List;

public record DashboardResult(
    UserResult user,
    List<AccountResult> accounts,
    List<AccountTransferResult> recentTransfers) {}
