package com.oda.domain.calendar.event;

import com.oda.domain.common.DomainEvent;
import com.oda.domain.calendar.EventType;

import java.time.LocalDateTime;

public record CalendarEventAddedEvent(
        Long eventId,
        Long userId,
        String title,
        EventType type,
        LocalDateTime occurredAt
) implements DomainEvent {

    @Override
    public LocalDateTime occurredAt() {
        return occurredAt;
    }
}
