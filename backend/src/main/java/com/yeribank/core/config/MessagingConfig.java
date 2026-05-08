package com.yeribank.core.config;

import com.yeribank.core.application.port.out.TransferEventPublisherPort;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class MessagingConfig {

  @Bean
  @ConditionalOnMissingBean(TransferEventPublisherPort.class)
  public TransferEventPublisherPort noOpTransferEventPublisher() {
    return event -> {};
  }
}
