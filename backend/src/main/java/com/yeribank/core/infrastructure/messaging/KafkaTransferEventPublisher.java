package com.yeribank.core.infrastructure.messaging;

import com.yeribank.core.application.event.TransferCreatedEvent;
import com.yeribank.core.application.port.out.TransferEventPublisherPort;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Component
@ConditionalOnProperty(name = "app.kafka.enabled", havingValue = "true", matchIfMissing = true)
public class KafkaTransferEventPublisher implements TransferEventPublisherPort {

  private final KafkaTemplate<String, Object> kafkaTemplate;
  private final String topic;

  public KafkaTransferEventPublisher(
      KafkaTemplate<String, Object> kafkaTemplate,
      @Value("${app.kafka.transfer-topic}") String topic) {
    this.kafkaTemplate = kafkaTemplate;
    this.topic = topic;
  }

  @Override
  public void publish(TransferCreatedEvent event) {
    kafkaTemplate.send(topic, event.transferId().toString(), event);
  }
}
