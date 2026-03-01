package com.oda.interfaces.api.dashboard.dto;

import com.oda.application.dashboard.dto.DashboardResult;
import com.oda.application.dashboard.dto.DDayItem;

import java.util.List;
import java.util.stream.Collectors;

public record DashboardResponse(
        TodaySummaryResponse todaySummary,
        List<DDayItemResponse> dDayItems,
        long unreadNotifications
) {
    public static DashboardResponse from(DashboardResult result) {
        TodaySummaryResponse summary = new TodaySummaryResponse(
                result.todaySummary().pendingTodos(),
                result.todaySummary().completedTodos(),
                result.todaySummary().upcomingEvents()
        );
        List<DDayItemResponse> dDayItemResponses = result.dDayItems().stream()
                .map(DDayItemResponse::from)
                .collect(Collectors.toList());
        return new DashboardResponse(summary, dDayItemResponses, result.unreadNotifications());
    }

    public record TodaySummaryResponse(long pendingTodos, long completedTodos, long upcomingEvents) {}
}
