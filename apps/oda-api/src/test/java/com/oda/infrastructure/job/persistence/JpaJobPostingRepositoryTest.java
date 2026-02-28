package com.oda.infrastructure.job.persistence;

import com.oda.infrastructure.security.encryption.AesEncryptor;
import com.oda.infrastructure.security.encryption.EncryptedLongConverter;
import com.oda.domain.job.Company;
import com.oda.domain.job.JobPosting;
import com.oda.domain.job.JobSource;
import com.oda.domain.job.SkillRequirement;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.test.context.ActiveProfiles;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@ActiveProfiles("test")
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Import({JpaJobPostingRepository.class, JobPostingMapper.class, AesEncryptor.class, EncryptedLongConverter.class})
class JpaJobPostingRepositoryTest {

    @Autowired
    private JpaJobPostingRepository repository;

    @Autowired
    private SpringDataJobPostingRepository springDataRepository;

    private static long externalIdCounter = System.currentTimeMillis();

    private JobPosting createSampleJob(String title, JobSource source) {
        Company company = new Company("테스트컴퍼니", "IT", "중견기업", "서울");
        JobPosting job = JobPosting.create(title, company, source);
        job.update("EXT-" + (externalIdCounter++), "백엔드 개발자를 모집합니다.",
                List.of(new SkillRequirement("Java", true, 1),
                        new SkillRequirement("Spring", true, 2)),
                3, "학사", "4000~5000만원", "서울", "정규직",
                "https://example.com/apply", null);
        return job;
    }

    @Test
    @DisplayName("채용공고 저장 및 조회")
    void 채용공고_저장_및_조회() {
        // given
        JobPosting job = createSampleJob("백엔드 개발자", JobSource.SARAMIN);

        // when
        JobPosting saved = repository.save(job);
        Optional<JobPosting> found = repository.findById(saved.getId());

        // then
        assertThat(saved.getId()).isNotNull();
        assertThat(found).isPresent();
        assertThat(found.get().getTitle()).isEqualTo("백엔드 개발자");
        assertThat(found.get().getSource()).isEqualTo(JobSource.SARAMIN);
        assertThat(found.get().getCompany().name()).isEqualTo("테스트컴퍼니");
    }

    @Test
    @DisplayName("소스별 채용공고 필터링")
    void 소스별_채용공고_필터링() {
        // given
        repository.save(createSampleJob("사람인 채용", JobSource.SARAMIN));
        repository.save(createSampleJob("원티드 채용", JobSource.WANTED));
        repository.save(createSampleJob("사람인 채용2", JobSource.SARAMIN));

        // when
        List<JobPosting> saraminJobs = repository.findBySource(JobSource.SARAMIN);
        List<JobPosting> wantedJobs = repository.findBySource(JobSource.WANTED);

        // then
        assertThat(saraminJobs).hasSize(2);
        assertThat(wantedJobs).hasSize(1);
        assertThat(saraminJobs).allMatch(j -> j.getSource() == JobSource.SARAMIN);
    }

    @Test
    @DisplayName("활성 채용공고만 조회")
    void 활성_채용공고만_조회() {
        // given
        JobPosting active1 = createSampleJob("활성 공고1", JobSource.SARAMIN);
        JobPosting active2 = createSampleJob("활성 공고2", JobSource.WANTED);
        JobPosting inactive = createSampleJob("비활성 공고", JobSource.JOBKOREA);
        inactive.deactivate();

        repository.save(active1);
        repository.save(active2);
        repository.save(inactive);

        // when
        List<JobPosting> activeJobs = repository.findByActiveTrue();

        // then
        assertThat(activeJobs).hasSize(2);
        assertThat(activeJobs).allMatch(JobPosting::isActive);
    }

    @Test
    @DisplayName("키워드로 채용공고 검색 - SpringData 직접 사용")
    void 키워드로_채용공고_검색() {
        // given
        repository.save(createSampleJob("시니어 백엔드 개발자", JobSource.SARAMIN));
        repository.save(createSampleJob("프론트엔드 개발자", JobSource.WANTED));
        repository.save(createSampleJob("백엔드 엔지니어", JobSource.JOBKOREA));

        // when
        Page<JobPostingJpaEntity> result = springDataRepository
                .findByTitleContainingIgnoreCase("백엔드", PageRequest.of(0, 10));

        // then
        assertThat(result.getTotalElements()).isEqualTo(2);
        assertThat(result.getContent()).allMatch(e -> e.getTitle().contains("백엔드"));
    }
}
