package com.oda.domain.calendar;

import java.time.LocalDate;

public record RecurrenceRule(String frequency, Integer interval, LocalDate until) {
}
