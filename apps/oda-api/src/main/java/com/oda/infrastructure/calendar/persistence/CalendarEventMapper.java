package com.oda.infrastructure.calendar.persistence;

import com.oda.domain.calendar.CalendarEvent;
import com.oda.domain.calendar.EventSource;
import com.oda.domain.calendar.RecurrenceRule;
import org.springframework.stereotype.Component;

import java.time.LocalDate;

@Component
public class CalendarEventMapper {

    public CalendarEvent toDomain(CalendarEventJpaEntity entity) {
        EventSource source = null;
        if (entity.getSourceType() != null) {
            source = new EventSource(entity.getSourceType(), entity.getSourceId());
        }

        RecurrenceRule recurrence = null;
        if (entity.getRecurrenceRule() != null) {
            recurrence = parseRecurrenceRule(entity.getRecurrenceRule());
        }

        return CalendarEvent.reconstruct(
                entity.getId(),
                entity.getUserId(),
                entity.getTitle(),
                entity.getDescription(),
                entity.getStartDate(),
                entity.getEndDate(),
                entity.getStartTime(),
                entity.getEndTime(),
                entity.getEventType(),
                source,
                entity.getActionUrl(),
                entity.isAllDay(),
                recurrence
        );
    }

    public CalendarEventJpaEntity toEntity(CalendarEvent domain) {
        String sourceType = domain.getSource() != null ? domain.getSource().sourceType() : null;
        Long sourceId = domain.getSource() != null ? domain.getSource().sourceId() : null;
        String recurrenceRule = domain.getRecurrence() != null ? serializeRecurrenceRule(domain.getRecurrence()) : null;

        if (domain.getId() != null) {
            return CalendarEventJpaEntity.withId(
                    domain.getId(),
                    domain.getUserId(),
                    domain.getTitle(),
                    domain.getDescription(),
                    domain.getStartDate(),
                    domain.getEndDate(),
                    domain.getStartTime(),
                    domain.getEndTime(),
                    domain.getType(),
                    sourceType,
                    sourceId,
                    domain.getActionUrl(),
                    domain.isAllDay(),
                    recurrenceRule
            );
        }

        return CalendarEventJpaEntity.create(
                domain.getUserId(),
                domain.getTitle(),
                domain.getDescription(),
                domain.getStartDate(),
                domain.getEndDate(),
                domain.getStartTime(),
                domain.getEndTime(),
                domain.getType(),
                sourceType,
                sourceId,
                domain.getActionUrl(),
                domain.isAllDay(),
                recurrenceRule
        );
    }

    private String serializeRecurrenceRule(RecurrenceRule rule) {
        // Simple format: frequency:interval:until
        String until = rule.until() != null ? rule.until().toString() : "";
        Integer interval = rule.interval() != null ? rule.interval() : 1;
        return rule.frequency() + ":" + interval + ":" + until;
    }

    private RecurrenceRule parseRecurrenceRule(String serialized) {
        String[] parts = serialized.split(":");
        if (parts.length < 3) return null;
        String frequency = parts[0];
        Integer interval = Integer.parseInt(parts[1]);
        LocalDate until = parts[2].isBlank() ? null : LocalDate.parse(parts[2]);
        return new RecurrenceRule(frequency, interval, until);
    }
}
