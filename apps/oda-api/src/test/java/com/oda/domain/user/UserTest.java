package com.oda.domain.user;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class UserTest {

    @Test
    @DisplayName("createFromOAuth_소셜로그인_생성")
    void createFromOAuth_소셜로그인_생성() {
        User user = User.createFromOAuth(OAuthProvider.KAKAO, "kakao123", "user@example.com", "홍길동");

        assertThat(user.getEmail()).isEqualTo("user@example.com");
        assertThat(user.getName()).isEqualTo("홍길동");
        assertThat(user.getOauthProvider()).isEqualTo(OAuthProvider.KAKAO);
        assertThat(user.getOauthId()).isEqualTo("kakao123");
        assertThat(user.isConsentPersonalInfo()).isFalse();
        assertThat(user.isConsentSensitiveInfo()).isFalse();
    }

    @Test
    @DisplayName("createFromOAuth_이메일_필수 - null이면 IllegalArgumentException")
    void createFromOAuth_이메일_필수_null() {
        assertThatThrownBy(() -> User.createFromOAuth(OAuthProvider.GOOGLE, "google456", null, "김철수"))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("email must not be null or blank");
    }

    @Test
    @DisplayName("createFromOAuth_이메일_필수 - 빈 문자열이면 IllegalArgumentException")
    void createFromOAuth_이메일_필수_blank() {
        assertThatThrownBy(() -> User.createFromOAuth(OAuthProvider.GOOGLE, "google456", "  ", "김철수"))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("email must not be null or blank");
    }

    @Test
    @DisplayName("updateConsent_동의_설정")
    void updateConsent_동의_설정() {
        User user = User.createFromOAuth(OAuthProvider.KAKAO, "kakao123", "user@example.com", "홍길동");

        user.updateConsent(true, true);

        assertThat(user.isConsentPersonalInfo()).isTrue();
        assertThat(user.isConsentSensitiveInfo()).isTrue();
    }

    @Test
    @DisplayName("updateConsent_동의_해제")
    void updateConsent_동의_해제() {
        User user = User.createFromOAuth(OAuthProvider.KAKAO, "kakao123", "user@example.com", "홍길동");
        user.updateConsent(true, true);

        user.updateConsent(false, false);

        assertThat(user.isConsentPersonalInfo()).isFalse();
        assertThat(user.isConsentSensitiveInfo()).isFalse();
    }

    @Test
    @DisplayName("linkOAuthAccount_계정_연동")
    void linkOAuthAccount_계정_연동() {
        User user = User.createFromOAuth(OAuthProvider.KAKAO, "kakao123", "user@example.com", "홍길동");

        user.linkOAuthAccount(OAuthProvider.GOOGLE, "google789");

        assertThat(user.getOauthProvider()).isEqualTo(OAuthProvider.GOOGLE);
        assertThat(user.getOauthId()).isEqualTo("google789");
    }

    @Test
    @DisplayName("updateName_이름_업데이트")
    void updateName_이름_업데이트() {
        User user = User.createFromOAuth(OAuthProvider.KAKAO, "kakao123", "user@example.com", "홍길동");

        user.updateName("김영희");

        assertThat(user.getName()).isEqualTo("김영희");
    }

    @Test
    @DisplayName("updateName_빈_이름_예외")
    void updateName_빈_이름_예외() {
        User user = User.createFromOAuth(OAuthProvider.KAKAO, "kakao123", "user@example.com", "홍길동");

        assertThatThrownBy(() -> user.updateName(""))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("name must not be null or blank");
    }
}
