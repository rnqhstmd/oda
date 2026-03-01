package com.oda.application.gamification;

import com.oda.application.gamification.dto.BadgeResult;
import com.oda.application.gamification.dto.GamificationResult;
import com.oda.domain.gamification.UserStreak;
import com.oda.domain.gamification.UserBadgeRepository;
import com.oda.domain.gamification.UserStreakRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

@Service
@Transactional
public class StreakService {

    private final UserStreakRepository userStreakRepository;
    private final UserBadgeRepository userBadgeRepository;

    public StreakService(UserStreakRepository userStreakRepository,
                         UserBadgeRepository userBadgeRepository) {
        this.userStreakRepository = userStreakRepository;
        this.userBadgeRepository = userBadgeRepository;
    }

    public GamificationResult recordActivity(Long userId) {
        UserStreak streak = userStreakRepository.findByUserId(userId)
                .orElseGet(() -> UserStreak.create(userId));
        streak.recordActivity(LocalDate.now());
        UserStreak saved = userStreakRepository.save(streak);

        List<BadgeResult> badges = userBadgeRepository.findByUserId(userId).stream()
                .map(b -> new BadgeResult(b.getBadge().name(), b.getEarnedAt()))
                .toList();

        return new GamificationResult(badges, saved.getCurrentStreak(), saved.getLongestStreak());
    }
}
