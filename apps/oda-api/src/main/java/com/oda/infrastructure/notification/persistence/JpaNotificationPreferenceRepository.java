package com.oda.infrastructure.notification.persistence;

import com.oda.domain.notification.NotificationChannel;
import com.oda.domain.notification.NotificationPreference;
import com.oda.domain.notification.NotificationType;
import com.oda.domain.notification.NotificationPreferenceRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Repository
@RequiredArgsConstructor
public class JpaNotificationPreferenceRepository implements NotificationPreferenceRepository {

    private final SpringDataNotificationPreferenceRepository springDataRepo;
    private final NotificationMapper notificationMapper;

    @Override
    public NotificationPreference save(NotificationPreference preference) {
        NotificationPreferenceJpaEntity entity = notificationMapper.toEntity(preference);
        NotificationPreferenceJpaEntity saved = springDataRepo.save(entity);
        return notificationMapper.toDomain(saved);
    }

    @Override
    public List<NotificationPreference> findByUserId(Long userId) {
        return springDataRepo.findByUserId(userId).stream()
                .map(notificationMapper::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public Optional<NotificationPreference> findByUserIdAndTypeAndChannel(Long userId, NotificationType type, NotificationChannel channel) {
        return springDataRepo.findByUserIdAndTypeAndChannel(userId, type, channel)
                .map(notificationMapper::toDomain);
    }
}
