package com.oda.domain.user;

import java.time.LocalDate;

public record WorkExperience(String companyName, String position, LocalDate startDate, LocalDate endDate, boolean isCurrent) {
    public WorkExperience {
        if (endDate != null && startDate != null && endDate.isBefore(startDate)) {
            throw new IllegalArgumentException("endDate must be after startDate");
        }
    }
}
