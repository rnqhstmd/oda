package com.oda.interfaces.api.notification.dto;

import com.oda.domain.notification.NotificationChannel;
import com.oda.domain.notification.NotificationPreference;
import com.oda.domain.notification.NotificationType;

public record PreferenceResponse(
        Long id,
        NotificationType type,
        NotificationChannel channel,
        boolean enabled
) {
    public static PreferenceResponse from(NotificationPreference pref) {
        return new PreferenceResponse(
                pref.getId(),
                pref.getType(),
                pref.getChannel(),
                pref.isEnabled()
        );
    }
}
