package com.oda.interfaces.api.calendar.dto;

import com.oda.application.calendar.dto.AddEventFromJobCommand;

public record AddFromJobRequest(Long jobPostingId) {
    public AddEventFromJobCommand toCommand() {
        return new AddEventFromJobCommand(jobPostingId);
    }
}
