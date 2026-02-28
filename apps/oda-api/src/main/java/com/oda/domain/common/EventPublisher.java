package com.oda.domain.common;

import com.oda.domain.common.DomainEvent;

public interface EventPublisher {
    void publish(DomainEvent event);
}
