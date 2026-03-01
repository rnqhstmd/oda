package com.oda.infrastructure.calendar.persistence;

import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;

public interface SpringDataCalendarEventRepository extends JpaRepository<CalendarEventJpaEntity, Long> {
    List<CalendarEventJpaEntity> findByUserIdAndStartDateBetween(Long userId, LocalDate start, LocalDate end);
}
