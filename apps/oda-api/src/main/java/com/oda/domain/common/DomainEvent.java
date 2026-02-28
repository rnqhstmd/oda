package com.oda.domain.common;

import java.time.LocalDateTime;

public interface DomainEvent {
    LocalDateTime occurredAt();
}
