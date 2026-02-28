package com.oda.infrastructure.notification.persistence;

import com.oda.domain.common.BaseEntity;
import com.oda.domain.notification.NotificationChannel;
import com.oda.domain.notification.NotificationType;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "notification_preferences")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class NotificationPreferenceJpaEntity extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Long userId;

    @Enumerated(EnumType.STRING)
    @Column(name = "notification_type", nullable = false)
    private NotificationType type;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private NotificationChannel channel;

    @Column(nullable = false)
    private boolean enabled;

    public static NotificationPreferenceJpaEntity create(Long userId, NotificationType type,
                                                         NotificationChannel channel, boolean enabled) {
        NotificationPreferenceJpaEntity entity = new NotificationPreferenceJpaEntity();
        entity.userId = userId;
        entity.type = type;
        entity.channel = channel;
        entity.enabled = enabled;
        return entity;
    }

    public void toggle(boolean enabled) {
        this.enabled = enabled;
    }
}
