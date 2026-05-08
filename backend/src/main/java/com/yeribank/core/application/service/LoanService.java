package com.yeribank.core.application.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.yeribank.core.application.dto.CreateLoanApplicationCommand;
import com.yeribank.core.application.dto.ListLoanApplicationsQuery;
import com.yeribank.core.application.dto.LoanApplicationResult;
import com.yeribank.core.application.dto.LoanOfferResult;
import com.yeribank.core.application.dto.LoanSimulationCommand;
import com.yeribank.core.application.exception.AppException;
import com.yeribank.core.application.port.in.LoanUseCase;
import com.yeribank.core.application.port.out.FinancialProfileRepositoryPort;
import com.yeribank.core.application.port.out.LoanApplicationRepositoryPort;
import com.yeribank.core.application.port.out.RiskProfileRepositoryPort;
import com.yeribank.core.application.port.out.UserRepositoryPort;
import com.yeribank.core.domain.model.FinancialProfile;
import com.yeribank.core.domain.model.LoanApplication;
import com.yeribank.core.domain.model.enums.LoanApplicationStatus;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.Clock;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class LoanService implements LoanUseCase {

  private static final BigDecimal ZERO = BigDecimal.ZERO.setScale(4, RoundingMode.HALF_UP);
  private static final BigDecimal ONE = new BigDecimal("1.0000");
  private static final BigDecimal TWELVE = new BigDecimal("12.0000");

  private final FinancialProfileRepositoryPort financialProfileRepository;
  private final RiskProfileRepositoryPort riskProfileRepository;
  private final LoanApplicationRepositoryPort loanApplicationRepository;
  private final UserRepositoryPort userRepository;
  private final ObjectMapper objectMapper;
  private final Clock clock;

  public LoanService(
      FinancialProfileRepositoryPort financialProfileRepository,
      RiskProfileRepositoryPort riskProfileRepository,
      LoanApplicationRepositoryPort loanApplicationRepository,
      UserRepositoryPort userRepository,
      ObjectMapper objectMapper,
      Clock clock) {
    this.financialProfileRepository = financialProfileRepository;
    this.riskProfileRepository = riskProfileRepository;
    this.loanApplicationRepository = loanApplicationRepository;
    this.userRepository = userRepository;
    this.objectMapper = objectMapper;
    this.clock = clock;
  }

  @Override
  @Transactional(readOnly = true)
  public LoanOfferResult simulate(LoanSimulationCommand command) {
    UUID targetUserId = resolveTargetUser(command.requestedUserId(), command.actorUserId(), command.actorAdmin());
    LoanEvaluation evaluation = evaluate(targetUserId, command.requestedAmount(), command.termMonths());
    return toOfferResult(evaluation);
  }

  @Override
  @Transactional
  public LoanApplicationResult createApplication(CreateLoanApplicationCommand command) {
    UUID targetUserId = resolveTargetUser(command.requestedUserId(), command.actorUserId(), command.actorAdmin());
    LoanEvaluation evaluation = evaluate(targetUserId, command.requestedAmount(), command.termMonths());
    LocalDateTime now = LocalDateTime.now(clock);

    LoanApplication application =
        loanApplicationRepository.save(
            LoanApplication.create(
                UUID.randomUUID(),
                targetUserId,
                evaluation.requestedAmount(),
                evaluation.approvedAmount(),
                evaluation.termMonths(),
                evaluation.annualInterestRate(),
                evaluation.estimatedInstallment(),
                evaluation.status(),
                writeSnapshot(evaluation),
                now));

    return toApplicationResult(application, evaluation.score(), evaluation.reasons());
  }

  @Override
  @Transactional(readOnly = true)
  public List<LoanApplicationResult> listApplications(ListLoanApplicationsQuery query) {
    UUID targetUserId = resolveListTargetUser(query.requestedUserId(), query.actorUserId(), query.actorAdmin());
    List<LoanApplication> applications =
        targetUserId == null
            ? loanApplicationRepository.findAll()
            : loanApplicationRepository.findAllByUserId(targetUserId);
    return applications.stream().map(this::toApplicationResult).toList();
  }

  private LoanEvaluation evaluate(UUID userId, BigDecimal requestedAmount, int termMonths) {
    validateInput(requestedAmount, termMonths);
    userRepository
        .findById(userId)
        .orElseThrow(() -> new AppException(HttpStatus.NOT_FOUND, "User not found"));

    FinancialProfile financialProfile =
        financialProfileRepository
            .findByUserId(userId)
            .orElseThrow(() -> new AppException(HttpStatus.NOT_FOUND, "Financial profile not found"));

    int baseRiskScore = riskProfileRepository.findByUserId(userId).map(profile -> profile.score()).orElse(650);
    BigDecimal annualInterestRate = resolveAnnualInterestRate(baseRiskScore);
    BigDecimal estimatedInstallment = estimateInstallment(requestedAmount, termMonths, annualInterestRate);
    BigDecimal disposableIncome =
        financialProfile.monthlyIncome().subtract(financialProfile.monthlyExpenses()).setScale(4, RoundingMode.HALF_UP);
    BigDecimal safeCapacity = disposableIncome.max(ZERO).multiply(new BigDecimal("0.3500")).setScale(4, RoundingMode.HALF_UP);
    BigDecimal debtToIncomeRatio = ratio(financialProfile.currentDebt(), financialProfile.monthlyIncome());
    BigDecimal installmentToDisposableRatio = ratio(estimatedInstallment, disposableIncome.max(ONE));
    BigDecimal annualIncomeEquivalent = financialProfile.monthlyIncome().multiply(TWELVE);
    BigDecimal requestToIncomeRatio = ratio(requestedAmount, annualIncomeEquivalent.max(ONE));

    int score = baseRiskScore;
    List<String> reasons = new ArrayList<>();

    if (disposableIncome.compareTo(ZERO) <= 0) {
      score -= 220;
      reasons.add("NEGATIVE_DISPOSABLE_INCOME");
    } else if (installmentToDisposableRatio.compareTo(new BigDecimal("0.7000")) > 0) {
      score -= 220;
      reasons.add("INSTALLMENT_EXCEEDS_SAFE_CAPACITY");
    } else if (installmentToDisposableRatio.compareTo(new BigDecimal("0.5000")) > 0) {
      score -= 120;
      reasons.add("INSTALLMENT_HIGH_VS_DISPOSABLE");
    } else if (installmentToDisposableRatio.compareTo(new BigDecimal("0.3000")) <= 0) {
      score += 40;
      reasons.add("INSTALLMENT_WITHIN_COMFORT_RANGE");
    }

    if (debtToIncomeRatio.compareTo(new BigDecimal("1.0000")) > 0) {
      score -= 150;
      reasons.add("DEBT_TO_INCOME_CRITICAL");
    } else if (debtToIncomeRatio.compareTo(new BigDecimal("0.6000")) > 0) {
      score -= 80;
      reasons.add("DEBT_TO_INCOME_ELEVATED");
    } else if (debtToIncomeRatio.compareTo(new BigDecimal("0.2500")) < 0) {
      score += 30;
      reasons.add("DEBT_LOAD_HEALTHY");
    }

    if (requestToIncomeRatio.compareTo(new BigDecimal("1.5000")) > 0) {
      score -= 80;
      reasons.add("REQUESTED_AMOUNT_AGGRESSIVE");
    } else if (requestToIncomeRatio.compareTo(new BigDecimal("0.6000")) < 0) {
      score += 20;
      reasons.add("REQUESTED_AMOUNT_ALIGNED_WITH_INCOME");
    }

    int normalizedScore = Math.max(0, Math.min(1000, score));
    BigDecimal approvedAmount = calculateApprovedAmount(requestedAmount, termMonths, annualInterestRate, safeCapacity);
    LoanApplicationStatus status = resolveStatus(normalizedScore, safeCapacity, estimatedInstallment, approvedAmount, requestedAmount);

    if (status == LoanApplicationStatus.REJECTED) {
      approvedAmount = ZERO;
    }

    return new LoanEvaluation(
        userId,
        requestedAmount.setScale(4, RoundingMode.HALF_UP),
        approvedAmount,
        termMonths,
        annualInterestRate,
        estimatedInstallment,
        status,
        normalizedScore,
        List.copyOf(reasons),
        baseRiskScore,
        disposableIncome,
        debtToIncomeRatio,
        installmentToDisposableRatio);
  }

  private UUID resolveTargetUser(UUID requestedUserId, UUID actorUserId, boolean actorAdmin) {
    if (requestedUserId == null || requestedUserId.equals(actorUserId)) {
      return actorUserId;
    }
    if (!actorAdmin) {
      throw new AppException(HttpStatus.FORBIDDEN, "You cannot operate this loan resource");
    }
    return requestedUserId;
  }

  private UUID resolveListTargetUser(UUID requestedUserId, UUID actorUserId, boolean actorAdmin) {
    if (requestedUserId == null) {
      return actorAdmin ? null : actorUserId;
    }
    return resolveTargetUser(requestedUserId, actorUserId, actorAdmin);
  }

  private void validateInput(BigDecimal requestedAmount, int termMonths) {
    if (requestedAmount == null || requestedAmount.compareTo(BigDecimal.ZERO) <= 0) {
      throw new AppException(HttpStatus.BAD_REQUEST, "Requested amount must be positive");
    }
    if (termMonths < 6 || termMonths > 72) {
      throw new AppException(HttpStatus.BAD_REQUEST, "Loan term must be between 6 and 72 months");
    }
  }

  private BigDecimal resolveAnnualInterestRate(int baseRiskScore) {
    if (baseRiskScore >= 750) {
      return new BigDecimal("0.0800");
    }
    if (baseRiskScore >= 550) {
      return new BigDecimal("0.1200");
    }
    return new BigDecimal("0.1800");
  }

  private BigDecimal estimateInstallment(
      BigDecimal requestedAmount, int termMonths, BigDecimal annualInterestRate) {
    BigDecimal totalCost =
        requestedAmount.multiply(ONE.add(annualInterestRate.multiply(BigDecimal.valueOf(termMonths)).divide(TWELVE, 8, RoundingMode.HALF_UP)));
    return totalCost.divide(BigDecimal.valueOf(termMonths), 4, RoundingMode.HALF_UP);
  }

  private BigDecimal calculateApprovedAmount(
      BigDecimal requestedAmount, int termMonths, BigDecimal annualInterestRate, BigDecimal safeCapacity) {
    if (safeCapacity.compareTo(ZERO) <= 0) {
      return ZERO;
    }
    BigDecimal financingFactor =
        ONE.add(annualInterestRate.multiply(BigDecimal.valueOf(termMonths)).divide(TWELVE, 8, RoundingMode.HALF_UP));
    BigDecimal capacityAmount =
        safeCapacity.multiply(BigDecimal.valueOf(termMonths)).divide(financingFactor, 4, RoundingMode.HALF_UP);
    BigDecimal normalizedRequested = requestedAmount.setScale(4, RoundingMode.HALF_UP);
    return capacityAmount.min(normalizedRequested).max(ZERO).setScale(4, RoundingMode.HALF_UP);
  }

  private LoanApplicationStatus resolveStatus(
      int score,
      BigDecimal safeCapacity,
      BigDecimal estimatedInstallment,
      BigDecimal approvedAmount,
      BigDecimal requestedAmount) {
    if (safeCapacity.compareTo(ZERO) <= 0
        || approvedAmount.compareTo(requestedAmount.multiply(new BigDecimal("0.3000")).setScale(4, RoundingMode.HALF_UP)) < 0) {
      return LoanApplicationStatus.REJECTED;
    }
    if (score >= 720 && estimatedInstallment.compareTo(safeCapacity.multiply(new BigDecimal("1.0500"))) <= 0) {
      return LoanApplicationStatus.APPROVED;
    }
    if (score >= 580) {
      return LoanApplicationStatus.REVIEW;
    }
    return LoanApplicationStatus.REJECTED;
  }

  private BigDecimal ratio(BigDecimal numerator, BigDecimal denominator) {
    if (denominator == null || denominator.compareTo(BigDecimal.ZERO) <= 0) {
      return ZERO;
    }
    return numerator.divide(denominator, 4, RoundingMode.HALF_UP);
  }

  private LoanOfferResult toOfferResult(LoanEvaluation evaluation) {
    return new LoanOfferResult(
        evaluation.requestedAmount(),
        evaluation.approvedAmount(),
        evaluation.termMonths(),
        evaluation.annualInterestRate(),
        evaluation.estimatedInstallment(),
        evaluation.status(),
        evaluation.score(),
        evaluation.reasons());
  }

  private LoanApplicationResult toApplicationResult(LoanApplication application) {
    Snapshot snapshot = readSnapshot(application.riskSnapshotJson());
    return new LoanApplicationResult(
        application.id(),
        application.userId(),
        application.requestedAmount(),
        application.approvedAmount(),
        application.termMonths(),
        application.annualInterestRate(),
        application.estimatedInstallment(),
        application.status(),
        snapshot.score(),
        snapshot.reasons(),
        application.createdAt());
  }

  private LoanApplicationResult toApplicationResult(
      LoanApplication application, int score, List<String> reasons) {
    return new LoanApplicationResult(
        application.id(),
        application.userId(),
        application.requestedAmount(),
        application.approvedAmount(),
        application.termMonths(),
        application.annualInterestRate(),
        application.estimatedInstallment(),
        application.status(),
        score,
        reasons,
        application.createdAt());
  }

  private String writeSnapshot(LoanEvaluation evaluation) {
    Map<String, Object> snapshot = new LinkedHashMap<>();
    snapshot.put("score", evaluation.score());
    snapshot.put("baseRiskScore", evaluation.baseRiskScore());
    snapshot.put("reasons", evaluation.reasons());
    snapshot.put("disposableIncome", evaluation.disposableIncome());
    snapshot.put("debtToIncomeRatio", evaluation.debtToIncomeRatio());
    snapshot.put("installmentToDisposableRatio", evaluation.installmentToDisposableRatio());
    try {
      return objectMapper.writeValueAsString(snapshot);
    } catch (JsonProcessingException ex) {
      throw new IllegalStateException("Unable to serialize loan snapshot", ex);
    }
  }

  private Snapshot readSnapshot(String riskSnapshotJson) {
    try {
      Map<String, Object> payload =
          objectMapper.readValue(riskSnapshotJson, new TypeReference<Map<String, Object>>() {});
      int score = ((Number) payload.getOrDefault("score", 0)).intValue();
      @SuppressWarnings("unchecked")
      List<String> reasons = (List<String>) payload.getOrDefault("reasons", List.of());
      return new Snapshot(score, reasons);
    } catch (JsonProcessingException ex) {
      throw new IllegalStateException("Unable to deserialize loan snapshot", ex);
    }
  }

  private record LoanEvaluation(
      UUID userId,
      BigDecimal requestedAmount,
      BigDecimal approvedAmount,
      int termMonths,
      BigDecimal annualInterestRate,
      BigDecimal estimatedInstallment,
      LoanApplicationStatus status,
      int score,
      List<String> reasons,
      int baseRiskScore,
      BigDecimal disposableIncome,
      BigDecimal debtToIncomeRatio,
      BigDecimal installmentToDisposableRatio) {}

  private record Snapshot(int score, List<String> reasons) {}
}
