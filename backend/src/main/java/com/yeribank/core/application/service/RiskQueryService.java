package com.yeribank.core.application.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.yeribank.core.application.dto.GetRiskProfileQuery;
import com.yeribank.core.application.dto.GetTransferRiskAssessmentQuery;
import com.yeribank.core.application.dto.RiskProfileResult;
import com.yeribank.core.application.dto.TransferRiskAssessmentResult;
import com.yeribank.core.application.exception.AppException;
import com.yeribank.core.application.port.in.RiskQueryUseCase;
import com.yeribank.core.application.port.out.RiskProfileRepositoryPort;
import com.yeribank.core.application.port.out.TransferRiskAssessmentRepositoryPort;
import com.yeribank.core.domain.model.TransferRiskAssessment;
import java.util.List;
import java.util.UUID;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

@Service
public class RiskQueryService implements RiskQueryUseCase {

  private final RiskProfileRepositoryPort riskProfileRepository;
  private final TransferRiskAssessmentRepositoryPort assessmentRepository;
  private final ObjectMapper objectMapper;

  public RiskQueryService(
      RiskProfileRepositoryPort riskProfileRepository,
      TransferRiskAssessmentRepositoryPort assessmentRepository,
      ObjectMapper objectMapper) {
    this.riskProfileRepository = riskProfileRepository;
    this.assessmentRepository = assessmentRepository;
    this.objectMapper = objectMapper;
  }

  @Override
  public RiskProfileResult getProfile(GetRiskProfileQuery query) {
    assertCanAccess(query.requestedUserId(), query.actorUserId(), query.actorAdmin());

    var profile =
        riskProfileRepository
            .findByUserId(query.requestedUserId())
            .orElseThrow(() -> new AppException(HttpStatus.NOT_FOUND, "Risk profile not found"));

    return new RiskProfileResult(
        profile.userId(),
        profile.score(),
        profile.lastAssessmentScore(),
        profile.riskLevel(),
        profile.alertCount90d(),
        profile.updatedAt());
  }

  @Override
  public TransferRiskAssessmentResult getTransferAssessment(GetTransferRiskAssessmentQuery query) {
    TransferRiskAssessment assessment =
        assessmentRepository
            .findByTransferId(query.transferId())
            .orElseThrow(
                () -> new AppException(HttpStatus.NOT_FOUND, "Transfer risk assessment not found"));

    assertCanAccess(assessment.userId(), query.actorUserId(), query.actorAdmin());

    return new TransferRiskAssessmentResult(
        assessment.transferId(),
        assessment.userId(),
        assessment.score(),
        assessment.decision(),
        assessment.riskLevel(),
        readReasons(assessment.reasonsJson()),
        assessment.createdAt());
  }

  private void assertCanAccess(UUID ownerUserId, UUID actorUserId, boolean actorAdmin) {
    if (!actorAdmin && !ownerUserId.equals(actorUserId)) {
      throw new AppException(HttpStatus.FORBIDDEN, "You cannot access this risk resource");
    }
  }

  private List<String> readReasons(String reasonsJson) {
    try {
      return objectMapper.readValue(reasonsJson, new TypeReference<List<String>>() {});
    } catch (JsonProcessingException ex) {
      throw new IllegalStateException("Unable to deserialize risk reasons", ex);
    }
  }
}
