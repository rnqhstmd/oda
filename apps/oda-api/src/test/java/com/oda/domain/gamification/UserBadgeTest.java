package com.oda.domain.gamification;

import com.oda.domain.gamification.Badge;
import com.oda.domain.gamification.UserBadge;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class UserBadgeTest {

    @Test
    @DisplayName("배지_생성")
    void 배지_생성() {
        UserBadge badge = UserBadge.create(1L, Badge.FIRST_LOGIN);

        assertThat(badge.getUserId()).isEqualTo(1L);
        assertThat(badge.getBadge()).isEqualTo(Badge.FIRST_LOGIN);
        assertThat(badge.getEarnedAt()).isNotNull();
        assertThat(badge.getEarnedAt()).isBeforeOrEqualTo(LocalDateTime.now());
    }

    @Test
    @DisplayName("배지_생성_userId_null_예외")
    void 배지_생성_userId_null_예외() {
        assertThatThrownBy(() -> UserBadge.create(null, Badge.FIRST_LOGIN))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("userId");
    }

    @Test
    @DisplayName("배지_생성_badge_null_예외")
    void 배지_생성_badge_null_예외() {
        assertThatThrownBy(() -> UserBadge.create(1L, null))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("badge");
    }

    @Test
    @DisplayName("중복_배지_방지_로직_서로_다른_배지는_다른_인스턴스")
    void 중복_배지_방지_로직_서로_다른_배지는_다른_인스턴스() {
        UserBadge badge1 = UserBadge.create(1L, Badge.FIRST_LOGIN);
        UserBadge badge2 = UserBadge.create(1L, Badge.PROFILE_COMPLETE);

        assertThat(badge1.getBadge()).isNotEqualTo(badge2.getBadge());
    }

    @Test
    @DisplayName("배지_재구성")
    void 배지_재구성() {
        LocalDateTime earnedAt = LocalDateTime.of(2024, 1, 1, 12, 0);
        UserBadge badge = UserBadge.reconstruct(1L, 2L, Badge.STREAK_7_DAYS, earnedAt);

        assertThat(badge.getId()).isEqualTo(1L);
        assertThat(badge.getUserId()).isEqualTo(2L);
        assertThat(badge.getBadge()).isEqualTo(Badge.STREAK_7_DAYS);
        assertThat(badge.getEarnedAt()).isEqualTo(earnedAt);
    }
}
