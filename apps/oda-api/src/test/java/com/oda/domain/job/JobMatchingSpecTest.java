package com.oda.domain.job;

import com.oda.domain.job.Company;
import com.oda.domain.job.GapAnalysis;
import com.oda.domain.job.JobPosting;
import com.oda.domain.job.JobSource;
import com.oda.domain.job.SkillRequirement;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class JobMatchingSpecTest {

    private static final Company COMPANY = new Company("테스트회사", "IT", "중견", "서울");

    private JobPosting jobWithSkills(List<SkillRequirement> skills, Integer expYears) {
        JobPosting job = JobPosting.create("백엔드 개발자", COMPANY, JobSource.SARAMIN);
        job.update(null, null, skills, expYears, null, null, null, null, null, null);
        return job;
    }

    @Test
    void 스킬_완전매칭_높은_점수() {
        List<SkillRequirement> required = List.of(
                new SkillRequirement("Java", true, 5),
                new SkillRequirement("Spring", true, 4)
        );
        JobPosting job = jobWithSkills(required, 0);
        List<String> userSkills = List.of("Java", "Spring");

        int score = JobMatchingSpec.calculateMatchScore(job, userSkills, 0, null);

        assertThat(score).isGreaterThanOrEqualTo(60);
    }

    @Test
    void 스킬_부분매칭_중간_점수() {
        List<SkillRequirement> required = List.of(
                new SkillRequirement("Java", true, 5),
                new SkillRequirement("Spring", true, 4),
                new SkillRequirement("Kubernetes", false, 2)
        );
        JobPosting job = jobWithSkills(required, 0);
        List<String> userSkills = List.of("Java");

        int score = JobMatchingSpec.calculateMatchScore(job, userSkills, 0, null);

        assertThat(score).isGreaterThan(0).isLessThan(70);
    }

    @Test
    void 스킬_없음_낮은_점수() {
        List<SkillRequirement> required = List.of(
                new SkillRequirement("Java", true, 5),
                new SkillRequirement("Spring", true, 4)
        );
        JobPosting job = jobWithSkills(required, 0);
        List<String> userSkills = List.of("Python");

        int score = JobMatchingSpec.calculateMatchScore(job, userSkills, 0, null);

        // skill score = 0 (60%), experience score = 100 (30%), education score = 100 (10%) = 40
        assertThat(score).isLessThan(50);
    }

    @Test
    void 경력_충족_점수_가산() {
        List<SkillRequirement> required = List.of(
                new SkillRequirement("Java", true, 5)
        );
        JobPosting job = jobWithSkills(required, 3);
        List<String> userSkills = List.of("Java");

        int scoreWithExp = JobMatchingSpec.calculateMatchScore(job, userSkills, 5, null);
        int scoreWithoutExp = JobMatchingSpec.calculateMatchScore(job, userSkills, 0, null);

        assertThat(scoreWithExp).isGreaterThan(scoreWithoutExp);
    }

    @Test
    void 경력_부족_점수_감산() {
        List<SkillRequirement> required = List.of(
                new SkillRequirement("Java", true, 5)
        );
        JobPosting job = jobWithSkills(required, 5);
        List<String> userSkills = List.of("Java");

        int scoreInsufficient = JobMatchingSpec.calculateMatchScore(job, userSkills, 1, null);
        int scoreSufficient = JobMatchingSpec.calculateMatchScore(job, userSkills, 5, null);

        assertThat(scoreInsufficient).isLessThan(scoreSufficient);
    }

    @Test
    void 갭분석_부족스킬_도출() {
        List<SkillRequirement> required = List.of(
                new SkillRequirement("Java", true, 5),
                new SkillRequirement("Kubernetes", false, 2)
        );
        JobPosting job = jobWithSkills(required, 0);
        List<String> userSkills = List.of("Java");

        GapAnalysis gap = JobMatchingSpec.analyzeGap(job, userSkills, 0);

        assertThat(gap.matchedSkills()).contains("java");
        assertThat(gap.missingSkills()).contains("kubernetes");
        assertThat(gap.matchPercentage()).isEqualTo(50);
    }

    @Test
    void 갭분석_추천사항_생성() {
        List<SkillRequirement> required = List.of(
                new SkillRequirement("Spring", true, 4)
        );
        JobPosting job = jobWithSkills(required, 3);
        List<String> userSkills = List.of();

        GapAnalysis gap = JobMatchingSpec.analyzeGap(job, userSkills, 1);

        assertThat(gap.recommendations()).isNotEmpty();
        assertThat(gap.recommendations()).anyMatch(r -> r.contains("spring") || r.contains("Spring"));
    }

    @Test
    void 갭분석_스킬_완전매칭_경력충족_추천내용() {
        List<SkillRequirement> required = List.of(
                new SkillRequirement("Java", true, 5)
        );
        JobPosting job = jobWithSkills(required, 2);
        List<String> userSkills = List.of("Java");

        GapAnalysis gap = JobMatchingSpec.analyzeGap(job, userSkills, 5);

        assertThat(gap.missingSkills()).isEmpty();
        assertThat(gap.matchPercentage()).isEqualTo(100);
        assertThat(gap.recommendations()).anyMatch(r -> r.contains("지원"));
    }
}
