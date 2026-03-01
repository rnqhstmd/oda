package com.oda.domain.job;

import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;

import java.time.LocalDate;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class JobPostingTest {

    private static final Company COMPANY = new Company("테스트회사", "IT", "중견", "서울");

    @Test
    void create_성공() {
        JobPosting job = JobPosting.create("백엔드 개발자", COMPANY, JobSource.SARAMIN);

        assertThat(job.getTitle()).isEqualTo("백엔드 개발자");
        assertThat(job.getCompany()).isEqualTo(COMPANY);
        assertThat(job.getSource()).isEqualTo(JobSource.SARAMIN);
        assertThat(job.isActive()).isTrue();
        assertThat(job.getLastSyncedAt()).isNotNull();
    }

    @Test
    void create_제목_null이면_예외() {
        assertThatThrownBy(() -> JobPosting.create(null, COMPANY, JobSource.WANTED))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void create_회사_null이면_예외() {
        assertThatThrownBy(() -> JobPosting.create("개발자", null, JobSource.WANTED))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void update_성공() {
        JobPosting job = JobPosting.create("백엔드 개발자", COMPANY, JobSource.SARAMIN);
        List<SkillRequirement> skills = List.of(
                new SkillRequirement("Java", true, 5),
                new SkillRequirement("Spring", true, 4)
        );

        job.update("EXT-001", "상세설명", skills, 3, "학사",
                "5000만원", "서울", "정규직", "https://example.com", LocalDate.now().plusDays(30));

        assertThat(job.getExternalId()).isEqualTo("EXT-001");
        assertThat(job.getDescription()).isEqualTo("상세설명");
        assertThat(job.getRequiredSkills()).hasSize(2);
        assertThat(job.getRequiredExperienceYears()).isEqualTo(3);
        assertThat(job.getSalary()).isEqualTo("5000만원");
    }

    @Test
    void isApplicationOpen_마감일_미래이면_true() {
        JobPosting job = JobPosting.create("개발자", COMPANY, JobSource.WANTED);
        job.update(null, null, null, null, null, null, null, null, null,
                LocalDate.now().plusDays(10));

        assertThat(job.isApplicationOpen()).isTrue();
    }

    @Test
    void isApplicationOpen_마감일_과거이면_false() {
        JobPosting job = JobPosting.create("개발자", COMPANY, JobSource.WANTED);
        job.update(null, null, null, null, null, null, null, null, null,
                LocalDate.now().minusDays(1));

        assertThat(job.isApplicationOpen()).isFalse();
    }

    @Test
    void isApplicationOpen_마감일_없으면_true() {
        JobPosting job = JobPosting.create("개발자", COMPANY, JobSource.WANTED);

        assertThat(job.isApplicationOpen()).isTrue();
    }

    @Test
    void isApplicationOpen_비활성이면_false() {
        JobPosting job = JobPosting.create("개발자", COMPANY, JobSource.WANTED);
        job.deactivate();

        assertThat(job.isApplicationOpen()).isFalse();
    }
}
