package com.oda.infrastructure.notification.persistence;

import com.oda.domain.notification.Notification;
import com.oda.domain.notification.NotificationPreference;
import org.springframework.stereotype.Component;

@Component
public class NotificationMapper {

    public Notification toDomain(NotificationJpaEntity entity) {
        return Notification.reconstruct(
                entity.getId(),
                entity.getUserId(),
                entity.getTitle(),
                entity.getBody(),
                entity.getType(),
                entity.getChannel(),
                entity.getReferenceId(),
                entity.getReferenceType(),
                entity.isRead()
        );
    }

    public NotificationJpaEntity toEntity(Notification domain) {
        return NotificationJpaEntity.create(
                domain.getUserId(),
                domain.getTitle(),
                domain.getBody(),
                domain.getType(),
                domain.getChannel(),
                domain.getReferenceId(),
                domain.getReferenceType(),
                domain.isRead()
        );
    }

    public NotificationPreference toDomain(NotificationPreferenceJpaEntity entity) {
        return NotificationPreference.reconstruct(
                entity.getId(),
                entity.getUserId(),
                entity.getType(),
                entity.getChannel(),
                entity.isEnabled()
        );
    }

    public NotificationPreferenceJpaEntity toEntity(NotificationPreference domain) {
        return NotificationPreferenceJpaEntity.create(
                domain.getUserId(),
                domain.getType(),
                domain.getChannel(),
                domain.isEnabled()
        );
    }
}
