package com.oda.infrastructure.calendar.persistence;

import com.oda.domain.calendar.EventType;
import com.oda.domain.common.BaseEntity;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalTime;

@Entity
@Table(name = "calendar_events")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class CalendarEventJpaEntity extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Long userId;

    @Column(nullable = false)
    private String title;

    @Column(columnDefinition = "TEXT")
    private String description;

    @Column(nullable = false)
    private LocalDate startDate;

    private LocalDate endDate;

    private LocalTime startTime;

    private LocalTime endTime;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private EventType eventType;

    private String sourceType;

    private Long sourceId;

    private String actionUrl;

    private boolean allDay;

    private String recurrenceRule;

    public static CalendarEventJpaEntity create(
            Long userId, String title, String description,
            LocalDate startDate, LocalDate endDate,
            LocalTime startTime, LocalTime endTime,
            EventType eventType, String sourceType, Long sourceId,
            String actionUrl, boolean allDay, String recurrenceRule) {
        CalendarEventJpaEntity entity = new CalendarEventJpaEntity();
        entity.userId = userId;
        entity.title = title;
        entity.description = description;
        entity.startDate = startDate;
        entity.endDate = endDate;
        entity.startTime = startTime;
        entity.endTime = endTime;
        entity.eventType = eventType;
        entity.sourceType = sourceType;
        entity.sourceId = sourceId;
        entity.actionUrl = actionUrl;
        entity.allDay = allDay;
        entity.recurrenceRule = recurrenceRule;
        return entity;
    }

    public static CalendarEventJpaEntity withId(
            Long id, Long userId, String title, String description,
            LocalDate startDate, LocalDate endDate,
            LocalTime startTime, LocalTime endTime,
            EventType eventType, String sourceType, Long sourceId,
            String actionUrl, boolean allDay, String recurrenceRule) {
        CalendarEventJpaEntity entity = create(userId, title, description, startDate, endDate,
                startTime, endTime, eventType, sourceType, sourceId, actionUrl, allDay, recurrenceRule);
        entity.id = id;
        return entity;
    }
}
