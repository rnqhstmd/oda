package com.oda.infrastructure.user.persistence;

import com.oda.infrastructure.security.encryption.AesEncryptor;
import com.oda.infrastructure.security.encryption.EncryptedLongConverter;
import com.oda.domain.user.OAuthProvider;
import com.oda.domain.user.User;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@ActiveProfiles("test")
@Import({UserMapper.class, AesEncryptor.class, EncryptedLongConverter.class, JpaUserRepository.class, JpaAuditingTestConfig.class})
class JpaUserRepositoryTest {

    @Autowired
    private JpaUserRepository userRepository;

    @Test
    @DisplayName("사용자 저장 및 ID로 조회")
    void 사용자_저장_및_조회() {
        User user = User.createFromOAuth(OAuthProvider.KAKAO, "kakao-123", "test@example.com", "홍길동");

        User saved = userRepository.save(user);

        assertThat(saved.getId()).isNotNull();
        Optional<User> found = userRepository.findById(saved.getId());
        assertThat(found).isPresent();
        assertThat(found.get().getEmail()).isEqualTo("test@example.com");
        assertThat(found.get().getName()).isEqualTo("홍길동");
        assertThat(found.get().getOauthProvider()).isEqualTo(OAuthProvider.KAKAO);
    }

    @Test
    @DisplayName("이메일로 사용자 조회")
    void 이메일로_조회() {
        User user = User.createFromOAuth(OAuthProvider.GOOGLE, "google-456", "google@example.com", "김철수");
        userRepository.save(user);

        Optional<User> found = userRepository.findByEmail("google@example.com");

        assertThat(found).isPresent();
        assertThat(found.get().getOauthProvider()).isEqualTo(OAuthProvider.GOOGLE);
        assertThat(found.get().getOauthId()).isEqualTo("google-456");
    }

    @Test
    @DisplayName("OAuth Provider와 ID로 사용자 조회")
    void OAuth_Provider로_조회() {
        User user = User.createFromOAuth(OAuthProvider.KAKAO, "kakao-789", "kakao@example.com", "이영희");
        userRepository.save(user);

        Optional<User> found = userRepository.findByOauthProviderAndOauthId(OAuthProvider.KAKAO, "kakao-789");

        assertThat(found).isPresent();
        assertThat(found.get().getEmail()).isEqualTo("kakao@example.com");
    }

    @Test
    @DisplayName("이메일 존재 여부 확인")
    void 이메일_존재_여부() {
        User user = User.createFromOAuth(OAuthProvider.KAKAO, "kakao-001", "exists@example.com", "테스트");
        userRepository.save(user);

        assertThat(userRepository.existsByEmail("exists@example.com")).isTrue();
        assertThat(userRepository.existsByEmail("notexists@example.com")).isFalse();
    }
}
