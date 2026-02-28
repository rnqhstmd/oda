package com.oda.application.simulation;

import com.oda.domain.job.Company;
import com.oda.domain.job.JobPosting;
import com.oda.domain.job.JobSource;
import com.oda.domain.job.SkillRequirement;
import com.oda.domain.job.JobPostingRepository;
import com.oda.application.simulation.CareerSimulationService;
import com.oda.domain.simulation.SimulationResult;
import com.oda.domain.user.UserProfile;
import com.oda.domain.user.UserProfileRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CareerSimulationServiceTest {

    @Mock
    private UserProfileRepository userProfileRepository;

    @Mock
    private JobPostingRepository jobPostingRepository;

    @InjectMocks
    private CareerSimulationService careerSimulationService;

    @Test
    @DisplayName("시뮬레이션_결과_생성")
    void 시뮬레이션_결과_생성() {
        Long userId = 1L;
        UserProfile profile = UserProfile.create(userId);
        profile.update(null, null, null, null, List.of(), List.of(),
                List.of(), List.of("Java"), List.of());

        JobPosting job = JobPosting.create("Backend Developer", new Company("TestCo", null, null, null), JobSource.SARAMIN);
        job.update(null, null,
                List.of(new SkillRequirement("Java", true, 5), new SkillRequirement("Spring", true, 5)),
                null, null, null, null, null, null, null);

        when(userProfileRepository.findByUserId(userId)).thenReturn(Optional.of(profile));
        when(jobPostingRepository.findByActiveTrue()).thenReturn(List.of(job));

        SimulationResult result = careerSimulationService.simulate(userId, "Backend");

        assertThat(result).isNotNull();
        assertThat(result.targetPosition()).isEqualTo("Backend");
        assertThat(result.careerPath()).isNotNull();
        assertThat(result.careerPath().steps()).isNotEmpty();
    }

    @Test
    @DisplayName("스킬_갭_분석")
    void 스킬_갭_분석() {
        Long userId = 1L;
        UserProfile profile = UserProfile.create(userId);
        profile.update(null, null, null, null, List.of(), List.of(),
                List.of(), List.of("Java"), List.of());

        JobPosting job = JobPosting.create("Backend Developer", new Company("TestCo", null, null, null), JobSource.SARAMIN);
        job.update(null, null,
                List.of(new SkillRequirement("Java", true, 5),
                        new SkillRequirement("Kotlin", true, 3),
                        new SkillRequirement("Docker", false, 2)),
                null, null, null, null, null, null, null);

        when(userProfileRepository.findByUserId(userId)).thenReturn(Optional.of(profile));
        when(jobPostingRepository.findByActiveTrue()).thenReturn(List.of(job));

        SimulationResult result = careerSimulationService.simulate(userId, "Backend Developer");

        assertThat(result.gapItems()).contains("Kotlin", "Docker");
        assertThat(result.gapItems()).doesNotContain("Java");
    }

    @Test
    @DisplayName("빈_프로필_처리")
    void 빈_프로필_처리() {
        Long userId = 1L;
        when(userProfileRepository.findByUserId(userId)).thenReturn(Optional.empty());
        when(jobPostingRepository.findByActiveTrue()).thenReturn(List.of());

        SimulationResult result = careerSimulationService.simulate(userId, "BACKEND_DEVELOPER");

        assertThat(result).isNotNull();
        assertThat(result.currentPosition()).isEqualTo("신입");
        assertThat(result.gapItems()).isEmpty();
    }

    @Test
    @DisplayName("매칭_잡이_없으면_빈_경력경로")
    void 매칭_잡이_없으면_빈_경력경로() {
        Long userId = 1L;
        UserProfile profile = UserProfile.create(userId);
        when(userProfileRepository.findByUserId(userId)).thenReturn(Optional.of(profile));
        when(jobPostingRepository.findByActiveTrue()).thenReturn(List.of());

        SimulationResult result = careerSimulationService.simulate(userId, "DATA_SCIENTIST");

        assertThat(result.careerPath().steps()).isNotNull();
    }

    @Test
    @DisplayName("경력자_포지션_처리")
    void 경력자_포지션_처리() {
        Long userId = 1L;
        UserProfile profile = UserProfile.create(userId);
        profile.update(null, null, null, null, List.of(), List.of(
                new com.oda.domain.user.WorkExperience("TestCo", "Dev", null, null, true)
        ), List.of(), List.of(), List.of());

        when(userProfileRepository.findByUserId(userId)).thenReturn(Optional.of(profile));
        when(jobPostingRepository.findByActiveTrue()).thenReturn(List.of());

        SimulationResult result = careerSimulationService.simulate(userId, "BACKEND_DEVELOPER");

        assertThat(result.currentPosition()).isEqualTo("경력");
    }
}
