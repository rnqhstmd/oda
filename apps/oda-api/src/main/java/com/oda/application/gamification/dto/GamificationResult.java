package com.oda.application.gamification.dto;

import java.util.List;

public record GamificationResult(List<BadgeResult> badges, int currentStreak, int longestStreak) {}
