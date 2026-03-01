package com.oda.domain.gamification;

import com.oda.domain.gamification.UserStreak;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;

import static org.assertj.core.api.Assertions.assertThat;

class UserStreakTest {

    @Test
    @DisplayName("연속_활동_기록")
    void 연속_활동_기록() {
        UserStreak streak = UserStreak.create(1L);
        LocalDate today = LocalDate.now();

        streak.recordActivity(today.minusDays(1));
        streak.recordActivity(today);

        assertThat(streak.getCurrentStreak()).isEqualTo(2);
        assertThat(streak.getLongestStreak()).isEqualTo(2);
    }

    @Test
    @DisplayName("하루_건너뛰면_리셋")
    void 하루_건너뛰면_리셋() {
        UserStreak streak = UserStreak.create(1L);
        LocalDate today = LocalDate.now();

        streak.recordActivity(today.minusDays(3));
        streak.recordActivity(today.minusDays(2));
        streak.recordActivity(today); // skipped yesterday → reset

        assertThat(streak.getCurrentStreak()).isEqualTo(1);
    }

    @Test
    @DisplayName("같은날_중복_기록_무시")
    void 같은날_중복_기록_무시() {
        UserStreak streak = UserStreak.create(1L);
        LocalDate today = LocalDate.now();

        streak.recordActivity(today);
        streak.recordActivity(today);

        assertThat(streak.getCurrentStreak()).isEqualTo(1);
    }

    @Test
    @DisplayName("최장_스트릭_갱신")
    void 최장_스트릭_갱신() {
        UserStreak streak = UserStreak.create(1L);
        LocalDate base = LocalDate.of(2024, 1, 1);

        for (int i = 0; i < 5; i++) {
            streak.recordActivity(base.plusDays(i));
        }
        assertThat(streak.getLongestStreak()).isEqualTo(5);

        // reset
        streak.recordActivity(base.plusDays(10));
        assertThat(streak.getCurrentStreak()).isEqualTo(1);
        assertThat(streak.getLongestStreak()).isEqualTo(5); // longest preserved
    }

    @Test
    @DisplayName("첫_활동_기록_스트릭_1")
    void 첫_활동_기록_스트릭_1() {
        UserStreak streak = UserStreak.create(1L);
        streak.recordActivity(LocalDate.now());

        assertThat(streak.getCurrentStreak()).isEqualTo(1);
        assertThat(streak.getLongestStreak()).isEqualTo(1);
        assertThat(streak.getLastActiveDate()).isEqualTo(LocalDate.now());
    }
}
