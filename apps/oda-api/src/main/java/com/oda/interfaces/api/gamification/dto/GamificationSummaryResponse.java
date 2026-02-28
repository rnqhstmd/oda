package com.oda.interfaces.api.gamification.dto;

import java.util.List;

public record GamificationSummaryResponse(List<BadgeResponse> badges, int currentStreak, int longestStreak) {}
