package com.oda.interfaces.api.dashboard.dto;

import com.oda.application.dashboard.dto.DDayItem;

public record DDayItemResponse(Long eventId, int dDay, String title, String type, String actionUrl) {
    public static DDayItemResponse from(DDayItem item) {
        return new DDayItemResponse(item.eventId(), item.dDay(), item.title(), item.type(), item.actionUrl());
    }
}
