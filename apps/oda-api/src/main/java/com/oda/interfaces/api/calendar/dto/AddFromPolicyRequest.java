package com.oda.interfaces.api.calendar.dto;

import com.oda.application.calendar.dto.AddEventFromPolicyCommand;

public record AddFromPolicyRequest(Long policyId) {
    public AddEventFromPolicyCommand toCommand() {
        return new AddEventFromPolicyCommand(policyId);
    }
}
