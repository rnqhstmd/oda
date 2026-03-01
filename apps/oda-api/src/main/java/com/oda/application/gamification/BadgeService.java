package com.oda.application.gamification;

import com.oda.domain.calendar.TodoStatus;
import com.oda.domain.calendar.TodoRepository;
import com.oda.application.gamification.dto.BadgeResult;
import com.oda.application.gamification.dto.GamificationResult;
import com.oda.domain.gamification.Badge;
import com.oda.domain.gamification.UserBadge;
import com.oda.domain.gamification.UserBadgeRepository;
import com.oda.domain.gamification.UserStreakRepository;
import com.oda.domain.user.UserProfileRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
public class BadgeService {

    private final UserBadgeRepository userBadgeRepository;
    private final UserStreakRepository userStreakRepository;
    private final UserProfileRepository userProfileRepository;
    private final TodoRepository todoRepository;

    public BadgeService(UserBadgeRepository userBadgeRepository,
                        UserStreakRepository userStreakRepository,
                        UserProfileRepository userProfileRepository,
                        TodoRepository todoRepository) {
        this.userBadgeRepository = userBadgeRepository;
        this.userStreakRepository = userStreakRepository;
        this.userProfileRepository = userProfileRepository;
        this.todoRepository = todoRepository;
    }

    public GamificationResult getBadges(Long userId) {
        List<UserBadge> badges = userBadgeRepository.findByUserId(userId);
        List<BadgeResult> badgeResults = badges.stream()
                .map(b -> new BadgeResult(b.getBadge().name(), b.getEarnedAt()))
                .toList();

        int currentStreak = userStreakRepository.findByUserId(userId)
                .map(s -> s.getCurrentStreak())
                .orElse(0);
        int longestStreak = userStreakRepository.findByUserId(userId)
                .map(s -> s.getLongestStreak())
                .orElse(0);

        return new GamificationResult(badgeResults, currentStreak, longestStreak);
    }

    public void checkAndAwardBadges(Long userId) {
        awardIfAbsent(userId, Badge.FIRST_LOGIN);

        userProfileRepository.findByUserId(userId).ifPresent(profile -> {
            boolean hasSkills = profile.getSkills() != null && !profile.getSkills().isEmpty();
            boolean hasCerts = profile.getCertifications() != null && !profile.getCertifications().isEmpty();
            boolean hasBirthDate = profile.getBirthDate() != null;
            if (hasSkills && hasBirthDate) {
                awardIfAbsent(userId, Badge.PROFILE_COMPLETE);
            }
            if (hasCerts) {
                awardIfAbsent(userId, Badge.FIRST_POLICY_SAVED);
            }
        });

        List<com.oda.domain.calendar.Todo> completedTodos =
                todoRepository.findByUserIdAndStatus(userId, TodoStatus.COMPLETED);
        int completedCount = completedTodos.size();
        if (completedCount >= 10) {
            awardIfAbsent(userId, Badge.TODO_MASTER_10);
        }
        if (completedCount >= 50) {
            awardIfAbsent(userId, Badge.TODO_MASTER_50);
        }

        userStreakRepository.findByUserId(userId).ifPresent(streak -> {
            if (streak.getCurrentStreak() >= 7) {
                awardIfAbsent(userId, Badge.STREAK_7_DAYS);
            }
            if (streak.getCurrentStreak() >= 30) {
                awardIfAbsent(userId, Badge.STREAK_30_DAYS);
            }
        });
    }

    private void awardIfAbsent(Long userId, Badge badge) {
        if (!userBadgeRepository.existsByUserIdAndBadge(userId, badge)) {
            userBadgeRepository.save(UserBadge.create(userId, badge));
        }
    }
}
