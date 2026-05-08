package com.yeribank.core.application.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.yeribank.core.application.dto.FraudAlertResult;
import com.yeribank.core.application.dto.ListFraudAlertsQuery;
import com.yeribank.core.application.dto.ReviewFraudAlertCommand;
import com.yeribank.core.application.exception.AppException;
import com.yeribank.core.application.port.in.FraudAlertUseCase;
import com.yeribank.core.application.port.out.FraudAlertRepositoryPort;
import com.yeribank.core.domain.model.FraudAlert;
import com.yeribank.core.domain.model.enums.FraudAlertStatus;
import java.time.Clock;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class FraudAlertService implements FraudAlertUseCase {

  private final FraudAlertRepositoryPort fraudAlertRepository;
  private final ObjectMapper objectMapper;
  private final Clock clock;

  public FraudAlertService(
      FraudAlertRepositoryPort fraudAlertRepository, ObjectMapper objectMapper, Clock clock) {
    this.fraudAlertRepository = fraudAlertRepository;
    this.objectMapper = objectMapper;
    this.clock = clock;
  }

  @Override
  public List<FraudAlertResult> listAlerts(ListFraudAlertsQuery query) {
    List<FraudAlert> alerts;
    if (query.actorAdmin()) {
      alerts =
          query.status() == null
              ? fraudAlertRepository.findAll()
              : fraudAlertRepository.findByStatus(query.status());
    } else {
      alerts =
          query.status() == null
              ? fraudAlertRepository.findByUserId(query.actorUserId())
              : fraudAlertRepository.findByUserIdAndStatus(query.actorUserId(), query.status());
    }

    return alerts.stream().map(this::toResult).toList();
  }

  @Override
  @Transactional
  public FraudAlertResult reviewAlert(ReviewFraudAlertCommand command) {
    if (!command.actorAdmin()) {
      throw new AppException(HttpStatus.FORBIDDEN, "Only admins can review fraud alerts");
    }
    if (command.status() == FraudAlertStatus.OPEN) {
      throw new AppException(HttpStatus.BAD_REQUEST, "Fraud alert review status must close the alert");
    }

    FraudAlert alert =
        fraudAlertRepository
            .findById(command.alertId())
            .orElseThrow(() -> new AppException(HttpStatus.NOT_FOUND, "Fraud alert not found"));

    return toResult(
        fraudAlertRepository.save(
            alert.review(command.status(), command.actorUserId(), LocalDateTime.now(clock))));
  }

  private FraudAlertResult toResult(FraudAlert alert) {
    return new FraudAlertResult(
        alert.id(),
        alert.transferId(),
        alert.userId(),
        alert.accountId(),
        alert.ruleCode(),
        alert.severity(),
        alert.status(),
        readDetails(alert.detailsJson()),
        alert.createdAt(),
        alert.reviewedAt(),
        alert.reviewedBy());
  }

  private Map<String, Object> readDetails(String detailsJson) {
    try {
      return objectMapper.readValue(detailsJson, new TypeReference<Map<String, Object>>() {});
    } catch (JsonProcessingException ex) {
      throw new IllegalStateException("Unable to deserialize fraud alert details", ex);
    }
  }
}
