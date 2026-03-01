package com.oda.interfaces.api.notification.dto;

import com.oda.application.notification.dto.UpdatePreferencesCommand;
import com.oda.domain.notification.NotificationChannel;
import com.oda.domain.notification.NotificationType;

import java.util.List;
import java.util.stream.Collectors;

public record UpdatePreferencesRequest(List<PreferenceItem> preferences) {

    public record PreferenceItem(
            NotificationType type,
            NotificationChannel channel,
            boolean enabled
    ) {}

    public UpdatePreferencesCommand toCommand() {
        List<UpdatePreferencesCommand.PreferenceItem> items = preferences.stream()
                .map(item -> new UpdatePreferencesCommand.PreferenceItem(item.type(), item.channel(), item.enabled()))
                .collect(Collectors.toList());
        return new UpdatePreferencesCommand(items);
    }
}
