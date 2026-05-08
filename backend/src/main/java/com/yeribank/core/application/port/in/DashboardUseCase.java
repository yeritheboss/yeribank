package com.yeribank.core.application.port.in;

import com.yeribank.core.application.dto.DashboardResult;
import java.util.UUID;

public interface DashboardUseCase {
  DashboardResult getMyDashboard(UUID actorUserId);
}
