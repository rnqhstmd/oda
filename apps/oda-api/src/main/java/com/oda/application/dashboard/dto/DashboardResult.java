package com.oda.application.dashboard.dto;

import java.util.List;

public record DashboardResult(TodaySummary todaySummary, List<DDayItem> dDayItems, long unreadNotifications) {
}
