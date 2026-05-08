package com.yeribank.core.application.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.yeribank.core.application.event.TransferCreatedEvent;
import com.yeribank.core.application.port.out.AccountRepositoryPort;
import com.yeribank.core.application.port.out.FraudAlertRepositoryPort;
import com.yeribank.core.application.port.out.RiskProfileRepositoryPort;
import com.yeribank.core.application.port.out.TransferRepositoryPort;
import com.yeribank.core.application.port.out.TransferRiskAssessmentRepositoryPort;
import com.yeribank.core.domain.model.Account;
import com.yeribank.core.domain.model.FraudAlert;
import com.yeribank.core.domain.model.enums.FraudAlertSeverity;
import com.yeribank.core.domain.model.RiskProfile;
import com.yeribank.core.domain.model.TransferRiskAssessment;
import java.math.BigDecimal;
import java.time.Clock;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Service
public class TransferRiskProcessingService {

  private static final BigDecimal HIGH_VALUE_THRESHOLD = new BigDecimal("1000.0000");
  private static final BigDecimal FIRST_COUNTERPARTY_THRESHOLD = new BigDecimal("1500.0000");

  private final AccountRepositoryPort accountRepository;
  private final TransferRepositoryPort transferRepository;
  private final TransferRiskAssessmentRepositoryPort assessmentRepository;
  private final FraudAlertRepositoryPort fraudAlertRepository;
  private final RiskProfileRepositoryPort riskProfileRepository;
  private final ObjectMapper objectMapper;
  private final Clock clock;

  public TransferRiskProcessingService(
      AccountRepositoryPort accountRepository,
      TransferRepositoryPort transferRepository,
      TransferRiskAssessmentRepositoryPort assessmentRepository,
      FraudAlertRepositoryPort fraudAlertRepository,
      RiskProfileRepositoryPort riskProfileRepository,
      ObjectMapper objectMapper,
      Clock clock) {
    this.accountRepository = accountRepository;
    this.transferRepository = transferRepository;
    this.assessmentRepository = assessmentRepository;
    this.fraudAlertRepository = fraudAlertRepository;
    this.riskProfileRepository = riskProfileRepository;
    this.objectMapper = objectMapper;
    this.clock = clock;
  }

  @Transactional(propagation = Propagation.REQUIRES_NEW)
  public void process(TransferCreatedEvent event) {
    if (assessmentRepository.findByTransferId(event.transferId()).isPresent()) {
      return;
    }

    Account fromAccount =
        accountRepository.findById(event.fromAccountId()).orElseThrow(() -> missingAccount(event.fromAccountId()));

    UUID userId = fromAccount.userId();
    LocalDateTime occurredAt = event.occurredAt() != null ? event.occurredAt() : LocalDateTime.now(clock);
    long transfersLastMinute =
        transferRepository.countByFromAccountIdAndCreatedAtAfter(
            event.fromAccountId(), occurredAt.minusMinutes(1));
    BigDecimal average30d =
        transferRepository.averageAmountByFromAccountIdAndCreatedAtAfter(
            event.fromAccountId(), occurredAt.minusDays(30));
    long transfersToCounterparty =
        transferRepository.countByFromAccountIdAndToAccountId(event.fromAccountId(), event.toAccountId());
    long previousAlerts24h =
        fraudAlertRepository.countByUserIdAndCreatedAtAfter(userId, occurredAt.minusHours(24));
    long existingAlerts90d =
        fraudAlertRepository.countByUserIdAndCreatedAtAfter(userId, occurredAt.minusDays(90));
    BigDecimal preTransferBalance = fromAccount.balance().add(event.amount());

    List<String> reasons = new ArrayList<>();
    List<FraudAlert> alerts = new ArrayList<>();
    int score = 700;

    if (transfersLastMinute > 10) {
      reasons.add("HIGH_FREQUENCY_1M");
      alerts.add(
          buildAlert(
              event,
              userId,
              "HIGH_FREQUENCY_1M",
              FraudAlertSeverity.HIGH,
              occurredAt,
              Map.of("transfersLastMinute", transfersLastMinute, "threshold", 10)));
      score -= 220;
    }

    if (average30d.compareTo(BigDecimal.ZERO) > 0
        && event.amount().compareTo(average30d.multiply(new BigDecimal("3"))) >= 0
        && event.amount().compareTo(HIGH_VALUE_THRESHOLD) >= 0) {
      reasons.add("AMOUNT_SPIKE_30D");
      alerts.add(
          buildAlert(
              event,
              userId,
              "AMOUNT_SPIKE_30D",
              FraudAlertSeverity.HIGH,
              occurredAt,
              Map.of("amount", event.amount(), "average30d", average30d)));
      score -= 120;
    }

    if (preTransferBalance.compareTo(BigDecimal.ZERO) > 0
        && event.amount().compareTo(preTransferBalance.multiply(new BigDecimal("0.40"))) >= 0) {
      reasons.add("BALANCE_DRAIN_40P");
      alerts.add(
          buildAlert(
              event,
              userId,
              "BALANCE_DRAIN_40P",
              FraudAlertSeverity.MEDIUM,
              occurredAt,
              Map.of("amount", event.amount(), "preTransferBalance", preTransferBalance)));
      score -= 110;
    }

    if (transfersToCounterparty == 1 && event.amount().compareTo(FIRST_COUNTERPARTY_THRESHOLD) >= 0) {
      reasons.add("FIRST_HIGH_VALUE_COUNTERPARTY");
      alerts.add(
          buildAlert(
              event,
              userId,
              "FIRST_HIGH_VALUE_COUNTERPARTY",
              FraudAlertSeverity.MEDIUM,
              occurredAt,
              Map.of("amount", event.amount(), "counterpartyTransfers", transfersToCounterparty)));
      score -= 90;
    }

    if (previousAlerts24h >= 3) {
      reasons.add("REPEATED_ALERTS_24H");
      alerts.add(
          buildAlert(
              event,
              userId,
              "REPEATED_ALERTS_24H",
              FraudAlertSeverity.HIGH,
              occurredAt,
              Map.of("previousAlerts24h", previousAlerts24h)));
      score -= 140;
    }

    if (reasons.isEmpty()) {
      score += 30;
    }
    if (existingAlerts90d >= 5) {
      score -= 50;
    }

    alerts.forEach(fraudAlertRepository::save);

    TransferRiskAssessment assessment =
        assessmentRepository.save(
            TransferRiskAssessment.create(
                UUID.randomUUID(),
                event.transferId(),
                userId,
                score,
                writeJson(reasons),
                occurredAt));

    transferRepository.updateRiskScore(event.transferId(), assessment.score());

    long nextAlertCount90d = existingAlerts90d + alerts.size();
    RiskProfile profile =
        riskProfileRepository
            .findByUserId(userId)
            .map(
                existing ->
                    existing.update(
                        blendedScore(existing.score(), assessment.score()),
                        assessment.score(),
                        nextAlertCount90d,
                        occurredAt))
            .orElseGet(
                () ->
                    RiskProfile.create(
                        UUID.randomUUID(),
                        userId,
                        assessment.score(),
                        assessment.score(),
                        nextAlertCount90d,
                        occurredAt));

    riskProfileRepository.save(profile);
  }

  private FraudAlert buildAlert(
      TransferCreatedEvent event,
      UUID userId,
      String ruleCode,
      FraudAlertSeverity severity,
      LocalDateTime occurredAt,
      Map<String, Object> details) {
    return FraudAlert.create(
        UUID.randomUUID(),
        event.transferId(),
        userId,
        event.fromAccountId(),
        ruleCode,
        severity,
        writeJson(details),
        occurredAt);
  }

  private int blendedScore(int currentScore, int newScore) {
    return (int) Math.round((currentScore * 0.7) + (newScore * 0.3));
  }

  private IllegalStateException missingAccount(UUID accountId) {
    return new IllegalStateException("Account not found for risk analysis: " + accountId);
  }

  private String writeJson(Object value) {
    try {
      return objectMapper.writeValueAsString(value);
    } catch (JsonProcessingException ex) {
      throw new IllegalStateException("Unable to serialize risk payload", ex);
    }
  }
}
