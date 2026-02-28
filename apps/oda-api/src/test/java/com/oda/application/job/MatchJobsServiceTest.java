package com.oda.application.job;

import com.oda.application.job.dto.JobMatchResult;
import com.oda.domain.job.Company;
import com.oda.domain.job.JobPosting;
import com.oda.domain.job.JobSource;
import com.oda.domain.job.SkillRequirement;
import com.oda.domain.job.JobPostingRepository;
import com.oda.domain.user.UserProfile;
import com.oda.domain.user.UserProfileRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;

@ExtendWith(MockitoExtension.class)
class MatchJobsServiceTest {

    @Mock
    private JobPostingRepository jobPostingRepository;

    @Mock
    private UserProfileRepository userProfileRepository;

    @InjectMocks
    private MatchJobsService matchJobsService;

    private static final Company COMPANY = new Company("테스트회사", "IT", "중견", "서울");

    private JobPosting createJobWithSkills(Long id, List<SkillRequirement> skills) {
        JobPosting job = JobPosting.create("백엔드 개발자", COMPANY, JobSource.SARAMIN);
        ReflectionTestUtils.setField(job, "id", id);
        job.update(null, null, skills, 0, null, null, null, null, null, null);
        return job;
    }

    @Test
    void 매칭_스킬_있는경우_결과_반환() {
        Long userId = 1L;
        UserProfile profile = UserProfile.create(userId);
        ReflectionTestUtils.setField(profile, "skills", List.of("Java", "Spring"));

        List<SkillRequirement> skills = List.of(
                new SkillRequirement("Java", true, 5),
                new SkillRequirement("Spring", true, 4)
        );
        JobPosting job = createJobWithSkills(1L, skills);

        given(userProfileRepository.findByUserId(userId)).willReturn(Optional.of(profile));
        given(jobPostingRepository.findByActiveTrue()).willReturn(List.of(job));

        List<JobMatchResult> results = matchJobsService.matchJobs(userId);

        assertThat(results).isNotEmpty();
        assertThat(results.get(0).matchScore()).isGreaterThan(0);
    }

    @Test
    void 프로필_없는경우_빈_스킬로_처리() {
        Long userId = 99L;
        List<SkillRequirement> skills = List.of(
                new SkillRequirement("Java", true, 5)
        );
        JobPosting job = createJobWithSkills(1L, skills);

        given(userProfileRepository.findByUserId(userId)).willReturn(Optional.empty());
        given(jobPostingRepository.findByActiveTrue()).willReturn(List.of(job));

        List<JobMatchResult> results = matchJobsService.matchJobs(userId);

        // no skills match; score may still be > 0 due to experience/education weights
        // but skill score portion is 0
        assertThat(results).allMatch(r -> r.gap().matchedSkills().isEmpty());
    }

    @Test
    void 매칭_점수_내림차순_정렬() {
        Long userId = 1L;
        UserProfile profile = UserProfile.create(userId);
        ReflectionTestUtils.setField(profile, "skills", List.of("Java", "Spring"));

        List<SkillRequirement> skills1 = List.of(new SkillRequirement("Java", true, 5));
        List<SkillRequirement> skills2 = List.of(
                new SkillRequirement("Java", true, 5),
                new SkillRequirement("Spring", true, 4)
        );
        JobPosting job1 = createJobWithSkills(1L, skills1);
        JobPosting job2 = createJobWithSkills(2L, skills2);

        given(userProfileRepository.findByUserId(userId)).willReturn(Optional.of(profile));
        given(jobPostingRepository.findByActiveTrue()).willReturn(List.of(job1, job2));

        List<JobMatchResult> results = matchJobsService.matchJobs(userId);

        assertThat(results).hasSize(2);
        assertThat(results.get(0).matchScore()).isGreaterThanOrEqualTo(results.get(1).matchScore());
    }

    @Test
    void 활성_공고_없으면_빈_결과() {
        Long userId = 1L;
        UserProfile profile = UserProfile.create(userId);

        given(userProfileRepository.findByUserId(userId)).willReturn(Optional.of(profile));
        given(jobPostingRepository.findByActiveTrue()).willReturn(List.of());

        List<JobMatchResult> results = matchJobsService.matchJobs(userId);

        assertThat(results).isEmpty();
    }
}
