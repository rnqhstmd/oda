package com.oda.application.gamification;

import com.oda.domain.calendar.Todo;
import com.oda.domain.calendar.TodoPriority;
import com.oda.domain.calendar.TodoStatus;
import com.oda.domain.calendar.TodoRepository;
import com.oda.application.gamification.dto.GamificationResult;
import com.oda.application.gamification.BadgeService;
import com.oda.domain.gamification.Badge;
import com.oda.domain.gamification.UserBadge;
import com.oda.domain.gamification.UserBadgeRepository;
import com.oda.domain.gamification.UserStreakRepository;
import com.oda.domain.user.Certification;
import com.oda.domain.user.UserProfile;
import com.oda.domain.user.UserProfileRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class BadgeServiceTest {

    @Mock
    private UserBadgeRepository userBadgeRepository;

    @Mock
    private UserStreakRepository userStreakRepository;

    @Mock
    private UserProfileRepository userProfileRepository;

    @Mock
    private TodoRepository todoRepository;

    @InjectMocks
    private BadgeService badgeService;

    @Test
    @DisplayName("첫_로그인_배지_수여")
    void 첫_로그인_배지_수여() {
        Long userId = 1L;
        when(userBadgeRepository.existsByUserIdAndBadge(userId, Badge.FIRST_LOGIN)).thenReturn(false);
        when(userBadgeRepository.save(any())).thenReturn(UserBadge.create(userId, Badge.FIRST_LOGIN));
        when(userProfileRepository.findByUserId(userId)).thenReturn(Optional.empty());
        when(todoRepository.findByUserIdAndStatus(userId, TodoStatus.COMPLETED)).thenReturn(List.of());
        when(userStreakRepository.findByUserId(userId)).thenReturn(Optional.empty());

        badgeService.checkAndAwardBadges(userId);

        verify(userBadgeRepository).save(any(UserBadge.class));
    }

    @Test
    @DisplayName("프로필_완성_배지")
    void 프로필_완성_배지() {
        Long userId = 1L;
        UserProfile profile = UserProfile.create(userId);
        profile.update(LocalDate.of(1990, 1, 1), "서울", "강남구", null,
                List.of(), List.of(), List.of(), List.of("Java", "Spring"), List.of());

        when(userBadgeRepository.existsByUserIdAndBadge(userId, Badge.FIRST_LOGIN)).thenReturn(true);
        when(userBadgeRepository.existsByUserIdAndBadge(userId, Badge.PROFILE_COMPLETE)).thenReturn(false);
        when(userBadgeRepository.save(any())).thenReturn(UserBadge.create(userId, Badge.PROFILE_COMPLETE));
        when(userProfileRepository.findByUserId(userId)).thenReturn(Optional.of(profile));
        when(todoRepository.findByUserIdAndStatus(userId, TodoStatus.COMPLETED)).thenReturn(List.of());
        when(userStreakRepository.findByUserId(userId)).thenReturn(Optional.empty());

        badgeService.checkAndAwardBadges(userId);

        verify(userBadgeRepository).save(argThat(b -> b.getBadge() == Badge.PROFILE_COMPLETE));
    }

    @Test
    @DisplayName("이미_보유한_배지_미수여")
    void 이미_보유한_배지_미수여() {
        Long userId = 1L;
        when(userBadgeRepository.existsByUserIdAndBadge(userId, Badge.FIRST_LOGIN)).thenReturn(true);
        when(userProfileRepository.findByUserId(userId)).thenReturn(Optional.empty());
        when(todoRepository.findByUserIdAndStatus(userId, TodoStatus.COMPLETED)).thenReturn(List.of());
        when(userStreakRepository.findByUserId(userId)).thenReturn(Optional.empty());

        badgeService.checkAndAwardBadges(userId);

        verify(userBadgeRepository, never()).save(argThat(b -> b.getBadge() == Badge.FIRST_LOGIN));
    }

    @Test
    @DisplayName("투두_10개_완료_배지_수여")
    void 투두_10개_완료_배지_수여() {
        Long userId = 1L;
        List<Todo> todos = List.of(
                Todo.reconstruct(1L, userId, null, "t1", null, TodoPriority.HIGH, TodoStatus.COMPLETED, null, LocalDateTime.now()),
                Todo.reconstruct(2L, userId, null, "t2", null, TodoPriority.HIGH, TodoStatus.COMPLETED, null, LocalDateTime.now()),
                Todo.reconstruct(3L, userId, null, "t3", null, TodoPriority.HIGH, TodoStatus.COMPLETED, null, LocalDateTime.now()),
                Todo.reconstruct(4L, userId, null, "t4", null, TodoPriority.HIGH, TodoStatus.COMPLETED, null, LocalDateTime.now()),
                Todo.reconstruct(5L, userId, null, "t5", null, TodoPriority.HIGH, TodoStatus.COMPLETED, null, LocalDateTime.now()),
                Todo.reconstruct(6L, userId, null, "t6", null, TodoPriority.HIGH, TodoStatus.COMPLETED, null, LocalDateTime.now()),
                Todo.reconstruct(7L, userId, null, "t7", null, TodoPriority.HIGH, TodoStatus.COMPLETED, null, LocalDateTime.now()),
                Todo.reconstruct(8L, userId, null, "t8", null, TodoPriority.HIGH, TodoStatus.COMPLETED, null, LocalDateTime.now()),
                Todo.reconstruct(9L, userId, null, "t9", null, TodoPriority.HIGH, TodoStatus.COMPLETED, null, LocalDateTime.now()),
                Todo.reconstruct(10L, userId, null, "t10", null, TodoPriority.HIGH, TodoStatus.COMPLETED, null, LocalDateTime.now())
        );

        when(userBadgeRepository.existsByUserIdAndBadge(userId, Badge.FIRST_LOGIN)).thenReturn(true);
        when(userBadgeRepository.existsByUserIdAndBadge(userId, Badge.TODO_MASTER_10)).thenReturn(false);
        when(userBadgeRepository.save(any())).thenReturn(UserBadge.create(userId, Badge.TODO_MASTER_10));
        when(userProfileRepository.findByUserId(userId)).thenReturn(Optional.empty());
        when(todoRepository.findByUserIdAndStatus(userId, TodoStatus.COMPLETED)).thenReturn(todos);
        when(userStreakRepository.findByUserId(userId)).thenReturn(Optional.empty());

        badgeService.checkAndAwardBadges(userId);

        verify(userBadgeRepository).save(argThat(b -> b.getBadge() == Badge.TODO_MASTER_10));
    }

    @Test
    @DisplayName("배지_목록_조회")
    void 배지_목록_조회() {
        Long userId = 1L;
        List<UserBadge> existingBadges = List.of(
                UserBadge.reconstruct(1L, userId, Badge.FIRST_LOGIN, LocalDateTime.now()),
                UserBadge.reconstruct(2L, userId, Badge.STREAK_7_DAYS, LocalDateTime.now())
        );
        when(userBadgeRepository.findByUserId(userId)).thenReturn(existingBadges);
        when(userStreakRepository.findByUserId(userId)).thenReturn(Optional.empty());

        GamificationResult result = badgeService.getBadges(userId);

        assertThat(result.badges()).hasSize(2);
        assertThat(result.currentStreak()).isEqualTo(0);
    }
}
