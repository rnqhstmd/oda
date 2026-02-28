package com.oda.domain.calendar;

import com.oda.domain.calendar.CalendarEvent;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface CalendarEventRepository {
    CalendarEvent save(CalendarEvent event);
    Optional<CalendarEvent> findById(Long id);
    List<CalendarEvent> findByUserIdAndStartDateBetween(Long userId, LocalDate start, LocalDate end);
    void deleteById(Long id);
}
