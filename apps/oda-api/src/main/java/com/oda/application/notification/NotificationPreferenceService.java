package com.oda.application.notification;

import com.oda.application.notification.dto.UpdatePreferencesCommand;
import com.oda.domain.notification.NotificationPreference;
import com.oda.domain.notification.NotificationPreferenceRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class NotificationPreferenceService {

    private final NotificationPreferenceRepository notificationPreferenceRepository;

    @Transactional(readOnly = true)
    public List<NotificationPreference> getPreferences(Long userId) {
        return notificationPreferenceRepository.findByUserId(userId);
    }

    public void updatePreferences(Long userId, UpdatePreferencesCommand command) {
        for (UpdatePreferencesCommand.PreferenceItem item : command.preferences()) {
            notificationPreferenceRepository
                    .findByUserIdAndTypeAndChannel(userId, item.type(), item.channel())
                    .ifPresentOrElse(
                            pref -> {
                                pref.toggle(item.enabled());
                                notificationPreferenceRepository.save(pref);
                            },
                            () -> notificationPreferenceRepository.save(
                                    NotificationPreference.create(userId, item.type(), item.channel(), item.enabled())
                            )
                    );
        }
    }
}
