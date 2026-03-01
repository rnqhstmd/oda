package com.oda.domain.notification;

import com.oda.domain.common.BaseEntity;
import lombok.Getter;

@Getter
public class NotificationPreference extends BaseEntity {

    private Long id;
    private Long userId;
    private NotificationType type;
    private NotificationChannel channel;
    private boolean enabled;

    private NotificationPreference() {}

    public static NotificationPreference create(Long userId, NotificationType type,
                                                NotificationChannel channel, boolean enabled) {
        NotificationPreference pref = new NotificationPreference();
        pref.userId = userId;
        pref.type = type;
        pref.channel = channel;
        pref.enabled = enabled;
        return pref;
    }

    public static NotificationPreference reconstruct(Long id, Long userId, NotificationType type,
                                                     NotificationChannel channel, boolean enabled) {
        NotificationPreference pref = new NotificationPreference();
        pref.id = id;
        pref.userId = userId;
        pref.type = type;
        pref.channel = channel;
        pref.enabled = enabled;
        return pref;
    }

    public void toggle(boolean enabled) {
        this.enabled = enabled;
    }
}
