package com.yeribank.core.application.service;

import com.yeribank.core.application.event.TransferCreatedEvent;
import com.yeribank.core.application.port.out.TransferEventPublisherPort;
import org.springframework.stereotype.Component;

@Component
public class TransferPostCommitHandler {

  private final TransferRiskProcessingService transferRiskProcessingService;
  private final TransferEventPublisherPort transferEventPublisher;

  public TransferPostCommitHandler(
      TransferRiskProcessingService transferRiskProcessingService,
      TransferEventPublisherPort transferEventPublisher) {
    this.transferRiskProcessingService = transferRiskProcessingService;
    this.transferEventPublisher = transferEventPublisher;
  }

  public void handle(TransferCreatedEvent event) {
    transferRiskProcessingService.process(event);
    transferEventPublisher.publish(event);
  }
}
