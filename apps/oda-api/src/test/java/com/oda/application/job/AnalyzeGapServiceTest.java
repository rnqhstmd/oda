package com.oda.application.job;

import com.oda.support.error.CoreException;
import com.oda.support.error.ErrorType;
import com.oda.domain.job.Company;
import com.oda.domain.job.GapAnalysis;
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
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.BDDMockito.given;

@ExtendWith(MockitoExtension.class)
class AnalyzeGapServiceTest {

    @Mock
    private JobPostingRepository jobPostingRepository;

    @Mock
    private UserProfileRepository userProfileRepository;

    @InjectMocks
    private AnalyzeGapService analyzeGapService;

    private static final Company COMPANY = new Company("테스트회사", "IT", "중견", "서울");

    @Test
    void 갭분석_매칭스킬_도출() {
        Long jobId = 1L;
        Long userId = 1L;

        JobPosting job = JobPosting.create("백엔드 개발자", COMPANY, JobSource.SARAMIN);
        ReflectionTestUtils.setField(job, "id", jobId);
        job.update(null, null,
                List.of(new SkillRequirement("Java", true, 5),
                        new SkillRequirement("Spring", true, 4)),
                0, null, null, null, null, null, null);

        UserProfile profile = UserProfile.create(userId);
        ReflectionTestUtils.setField(profile, "skills", List.of("Java"));

        given(jobPostingRepository.findById(jobId)).willReturn(Optional.of(job));
        given(userProfileRepository.findByUserId(userId)).willReturn(Optional.of(profile));

        GapAnalysis gap = analyzeGapService.analyzeGap(jobId, userId);

        assertThat(gap.matchedSkills()).contains("java");
        assertThat(gap.missingSkills()).contains("spring");
        assertThat(gap.matchPercentage()).isEqualTo(50);
    }

    @Test
    void 갭분석_공고_없으면_예외() {
        Long jobId = 999L;
        Long userId = 1L;

        given(jobPostingRepository.findById(jobId)).willReturn(Optional.empty());

        assertThatThrownBy(() -> analyzeGapService.analyzeGap(jobId, userId))
                .isInstanceOf(CoreException.class)
                .satisfies(e -> org.assertj.core.api.Assertions.assertThat(((CoreException) e).getErrorType()).isEqualTo(ErrorType.NOT_FOUND));
    }

    @Test
    void 갭분석_프로필_없으면_모두_부족스킬() {
        Long jobId = 1L;
        Long userId = 99L;

        JobPosting job = JobPosting.create("백엔드 개발자", COMPANY, JobSource.SARAMIN);
        ReflectionTestUtils.setField(job, "id", jobId);
        job.update(null, null,
                List.of(new SkillRequirement("Java", true, 5)),
                0, null, null, null, null, null, null);

        given(jobPostingRepository.findById(jobId)).willReturn(Optional.of(job));
        given(userProfileRepository.findByUserId(userId)).willReturn(Optional.empty());

        GapAnalysis gap = analyzeGapService.analyzeGap(jobId, userId);

        assertThat(gap.matchedSkills()).isEmpty();
        assertThat(gap.missingSkills()).contains("java");
        assertThat(gap.matchPercentage()).isEqualTo(0);
    }

    @Test
    void 갭분석_추천사항_포함() {
        Long jobId = 1L;
        Long userId = 1L;

        JobPosting job = JobPosting.create("백엔드 개발자", COMPANY, JobSource.SARAMIN);
        ReflectionTestUtils.setField(job, "id", jobId);
        job.update(null, null,
                List.of(new SkillRequirement("Kubernetes", true, 3)),
                0, null, null, null, null, null, null);

        UserProfile profile = UserProfile.create(userId);
        ReflectionTestUtils.setField(profile, "skills", List.of());

        given(jobPostingRepository.findById(jobId)).willReturn(Optional.of(job));
        given(userProfileRepository.findByUserId(userId)).willReturn(Optional.of(profile));

        GapAnalysis gap = analyzeGapService.analyzeGap(jobId, userId);

        assertThat(gap.recommendations()).isNotEmpty();
    }
}
