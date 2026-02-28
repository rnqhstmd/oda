package com.oda.application.notification.dto;

import com.oda.domain.notification.NotificationChannel;
import com.oda.domain.notification.NotificationType;

import java.util.List;

public record UpdatePreferencesCommand(List<PreferenceItem> preferences) {
    public record PreferenceItem(
            NotificationType type,
            NotificationChannel channel,
            boolean enabled
    ) {}
}
