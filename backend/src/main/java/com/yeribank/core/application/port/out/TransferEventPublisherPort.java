package com.yeribank.core.application.port.out;

import com.yeribank.core.application.event.TransferCreatedEvent;

public interface TransferEventPublisherPort {
  void publish(TransferCreatedEvent event);
}
