package com.oda.application.user;

import com.oda.application.user.dto.ProfileResult;
import com.oda.application.user.dto.UpdateProfileCommand;
import com.oda.support.error.CoreException;
import com.oda.support.error.ErrorType;
import com.oda.domain.user.EmploymentStatus;
import com.oda.domain.user.UserProfile;
import com.oda.domain.user.UserProfileRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class ProfileServiceTest {

    @Mock
    private UserProfileRepository userProfileRepository;

    @InjectMocks
    private ProfileService profileService;

    @Test
    void 프로필_조회_성공() {
        // given
        Long userId = 1L;
        UserProfile profile = UserProfile.create(userId);
        ReflectionTestUtils.setField(profile, "id", 10L);
        ReflectionTestUtils.setField(profile, "birthDate", LocalDate.of(1990, 1, 1));

        given(userProfileRepository.findByUserId(userId)).willReturn(Optional.of(profile));

        // when
        ProfileResult result = profileService.getProfile(userId);

        // then
        assertThat(result.userId()).isEqualTo(userId);
        assertThat(result.id()).isEqualTo(10L);
        assertThat(result.birthDate()).isEqualTo(LocalDate.of(1990, 1, 1));
    }

    @Test
    void 프로필_조회_존재하지않음() {
        // given
        Long userId = 99L;
        given(userProfileRepository.findByUserId(userId)).willReturn(Optional.empty());

        // when & then
        assertThatThrownBy(() -> profileService.getProfile(userId))
                .isInstanceOf(CoreException.class)
                .satisfies(e -> org.assertj.core.api.Assertions.assertThat(((CoreException) e).getErrorType()).isEqualTo(ErrorType.NOT_FOUND));
    }

    @Test
    void 프로필_업데이트_성공() {
        // given
        Long userId = 1L;
        UserProfile profile = UserProfile.create(userId);
        ReflectionTestUtils.setField(profile, "id", 10L);

        UpdateProfileCommand command = new UpdateProfileCommand(
                LocalDate.of(1995, 5, 15),
                "서울",
                "강남구",
                EmploymentStatus.EMPLOYED,
                List.of(),
                List.of(),
                List.of(),
                List.of("Java", "Spring"),
                List.of("Backend")
        );

        given(userProfileRepository.findByUserId(userId)).willReturn(Optional.of(profile));
        given(userProfileRepository.save(profile)).willReturn(profile);

        // when
        ProfileResult result = profileService.updateProfile(userId, command);

        // then
        assertThat(result.sido()).isEqualTo("서울");
        assertThat(result.sigungu()).isEqualTo("강남구");
        assertThat(result.employmentStatus()).isEqualTo(EmploymentStatus.EMPLOYED);
        verify(userProfileRepository).save(profile);
    }

    @Test
    void 프로필_최초_생성() {
        // given
        Long userId = 2L;
        UpdateProfileCommand command = new UpdateProfileCommand(
                LocalDate.of(2000, 3, 20),
                "부산",
                "해운대구",
                EmploymentStatus.STUDENT,
                List.of(),
                List.of(),
                List.of(),
                List.of(),
                List.of()
        );

        given(userProfileRepository.findByUserId(userId)).willReturn(Optional.empty());
        UserProfile newProfile = UserProfile.create(userId);
        given(userProfileRepository.save(any(UserProfile.class))).willReturn(newProfile);

        // when
        ProfileResult result = profileService.updateProfile(userId, command);

        // then
        assertThat(result.userId()).isEqualTo(userId);
        verify(userProfileRepository).save(any(UserProfile.class));
    }
}
