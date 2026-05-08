package com.yeribank.core.application.service;

import com.yeribank.core.application.dto.FinancialProfileCommand;
import com.yeribank.core.application.dto.FinancialProfileResult;
import com.yeribank.core.application.dto.GetFinancialProfileQuery;
import com.yeribank.core.application.exception.AppException;
import com.yeribank.core.application.port.in.FinancialProfileUseCase;
import com.yeribank.core.application.port.out.FinancialProfileRepositoryPort;
import com.yeribank.core.application.port.out.UserRepositoryPort;
import com.yeribank.core.domain.model.FinancialProfile;
import java.math.BigDecimal;
import java.time.Clock;
import java.time.LocalDateTime;
import java.util.UUID;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class FinancialProfileService implements FinancialProfileUseCase {

  private final FinancialProfileRepositoryPort financialProfileRepository;
  private final UserRepositoryPort userRepository;
  private final Clock clock;

  public FinancialProfileService(
      FinancialProfileRepositoryPort financialProfileRepository,
      UserRepositoryPort userRepository,
      Clock clock) {
    this.financialProfileRepository = financialProfileRepository;
    this.userRepository = userRepository;
    this.clock = clock;
  }

  @Override
  @Transactional
  public FinancialProfileResult upsert(FinancialProfileCommand command) {
    UUID targetUserId = resolveTargetUser(command.requestedUserId(), command.actorUserId(), command.actorAdmin());
    validateAmounts(command.monthlyIncome(), command.monthlyExpenses(), command.currentDebt());

    userRepository
        .findById(targetUserId)
        .orElseThrow(() -> new AppException(HttpStatus.NOT_FOUND, "User not found"));

    LocalDateTime now = LocalDateTime.now(clock);
    FinancialProfile saved =
        financialProfileRepository
            .findByUserId(targetUserId)
            .map(
                existing ->
                    existing.update(
                        command.monthlyIncome(),
                        command.monthlyExpenses(),
                        command.currentDebt(),
                        now))
            .orElseGet(
                () ->
                    FinancialProfile.create(
                        UUID.randomUUID(),
                        targetUserId,
                        command.monthlyIncome(),
                        command.monthlyExpenses(),
                        command.currentDebt(),
                        now));

    return toResult(financialProfileRepository.save(saved));
  }

  @Override
  @Transactional(readOnly = true)
  public FinancialProfileResult get(GetFinancialProfileQuery query) {
    UUID targetUserId = resolveTargetUser(query.requestedUserId(), query.actorUserId(), query.actorAdmin());

    return financialProfileRepository
        .findByUserId(targetUserId)
        .map(this::toResult)
        .orElseThrow(() -> new AppException(HttpStatus.NOT_FOUND, "Financial profile not found"));
  }

  private UUID resolveTargetUser(UUID requestedUserId, UUID actorUserId, boolean actorAdmin) {
    if (requestedUserId == null || requestedUserId.equals(actorUserId)) {
      return actorUserId;
    }
    if (!actorAdmin) {
      throw new AppException(HttpStatus.FORBIDDEN, "You cannot access this financial profile");
    }
    return requestedUserId;
  }

  private void validateAmounts(
      BigDecimal monthlyIncome, BigDecimal monthlyExpenses, BigDecimal currentDebt) {
    if (monthlyIncome == null || monthlyExpenses == null || currentDebt == null) {
      throw new AppException(HttpStatus.BAD_REQUEST, "Financial profile fields are required");
    }
    if (monthlyIncome.compareTo(BigDecimal.ZERO) < 0
        || monthlyExpenses.compareTo(BigDecimal.ZERO) < 0
        || currentDebt.compareTo(BigDecimal.ZERO) < 0) {
      throw new AppException(HttpStatus.BAD_REQUEST, "Financial profile values cannot be negative");
    }
  }

  private FinancialProfileResult toResult(FinancialProfile profile) {
    return new FinancialProfileResult(
        profile.userId(),
        profile.monthlyIncome(),
        profile.monthlyExpenses(),
        profile.currentDebt(),
        profile.updatedAt());
  }
}
