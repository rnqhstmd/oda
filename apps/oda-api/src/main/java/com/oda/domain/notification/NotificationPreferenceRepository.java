package com.oda.domain.notification;

import com.oda.domain.notification.NotificationChannel;
import com.oda.domain.notification.NotificationPreference;
import com.oda.domain.notification.NotificationType;

import java.util.List;
import java.util.Optional;

public interface NotificationPreferenceRepository {
    NotificationPreference save(NotificationPreference preference);
    List<NotificationPreference> findByUserId(Long userId);
    Optional<NotificationPreference> findByUserIdAndTypeAndChannel(Long userId, NotificationType type, NotificationChannel channel);
}
