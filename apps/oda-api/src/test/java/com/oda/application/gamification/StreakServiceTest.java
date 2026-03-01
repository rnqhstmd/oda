package com.oda.application.gamification;

import com.oda.application.gamification.dto.GamificationResult;
import com.oda.application.gamification.StreakService;
import com.oda.domain.gamification.UserStreak;
import com.oda.domain.gamification.UserBadgeRepository;
import com.oda.domain.gamification.UserStreakRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class StreakServiceTest {

    @Mock
    private UserStreakRepository userStreakRepository;

    @Mock
    private UserBadgeRepository userBadgeRepository;

    @InjectMocks
    private StreakService streakService;

    @Test
    @DisplayName("활동_기록_스트릭_증가")
    void 활동_기록_스트릭_증가() {
        Long userId = 1L;
        UserStreak existing = UserStreak.reconstruct(1L, userId, 3, 5, LocalDate.now().minusDays(1));
        when(userStreakRepository.findByUserId(userId)).thenReturn(Optional.of(existing));
        UserStreak saved = UserStreak.reconstruct(1L, userId, 4, 5, LocalDate.now());
        when(userStreakRepository.save(any())).thenReturn(saved);
        when(userBadgeRepository.findByUserId(userId)).thenReturn(List.of());

        GamificationResult result = streakService.recordActivity(userId);

        assertThat(result.currentStreak()).isEqualTo(4);
        verify(userStreakRepository).save(any(UserStreak.class));
    }

    @Test
    @DisplayName("최장_스트릭_갱신")
    void 최장_스트릭_갱신() {
        Long userId = 1L;
        UserStreak existing = UserStreak.reconstruct(1L, userId, 9, 9, LocalDate.now().minusDays(1));
        when(userStreakRepository.findByUserId(userId)).thenReturn(Optional.of(existing));
        UserStreak saved = UserStreak.reconstruct(1L, userId, 10, 10, LocalDate.now());
        when(userStreakRepository.save(any())).thenReturn(saved);
        when(userBadgeRepository.findByUserId(userId)).thenReturn(List.of());

        GamificationResult result = streakService.recordActivity(userId);

        assertThat(result.longestStreak()).isEqualTo(10);
    }

    @Test
    @DisplayName("신규_유저_스트릭_생성")
    void 신규_유저_스트릭_생성() {
        Long userId = 99L;
        when(userStreakRepository.findByUserId(userId)).thenReturn(Optional.empty());
        UserStreak newStreak = UserStreak.reconstruct(null, userId, 1, 1, LocalDate.now());
        when(userStreakRepository.save(any())).thenReturn(newStreak);
        when(userBadgeRepository.findByUserId(userId)).thenReturn(List.of());

        GamificationResult result = streakService.recordActivity(userId);

        assertThat(result.currentStreak()).isEqualTo(1);
        verify(userStreakRepository).save(any(UserStreak.class));
    }

    @Test
    @DisplayName("같은날_기록_스트릭_유지")
    void 같은날_기록_스트릭_유지() {
        Long userId = 1L;
        UserStreak existing = UserStreak.reconstruct(1L, userId, 5, 5, LocalDate.now());
        when(userStreakRepository.findByUserId(userId)).thenReturn(Optional.of(existing));
        when(userStreakRepository.save(any())).thenReturn(existing);
        when(userBadgeRepository.findByUserId(userId)).thenReturn(List.of());

        GamificationResult result = streakService.recordActivity(userId);

        assertThat(result.currentStreak()).isEqualTo(5);
    }

    @Test
    @DisplayName("배지_목록_포함_반환")
    void 배지_목록_포함_반환() {
        Long userId = 1L;
        UserStreak existing = UserStreak.reconstruct(1L, userId, 1, 1, LocalDate.now().minusDays(1));
        when(userStreakRepository.findByUserId(userId)).thenReturn(Optional.of(existing));
        when(userStreakRepository.save(any())).thenReturn(existing);
        when(userBadgeRepository.findByUserId(userId)).thenReturn(List.of());

        GamificationResult result = streakService.recordActivity(userId);

        assertThat(result.badges()).isNotNull();
    }
}
