package com.oda.infrastructure.policy.persistence;

import com.oda.infrastructure.security.encryption.AesEncryptor;
import com.oda.domain.policy.EligibilityCriteria;
import com.oda.domain.policy.Policy;
import com.oda.domain.policy.PolicyCategory;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.test.context.ActiveProfiles;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@ActiveProfiles("test")
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Import({JpaPolicyRepository.class, PolicyMapper.class, AesEncryptor.class})
class JpaPolicyRepositoryTest {

    @Autowired
    private JpaPolicyRepository jpaPolicyRepository;

    @Autowired
    private SpringDataPolicyRepository springDataPolicyRepository;

    private Policy createPolicy(String title, PolicyCategory category) {
        EligibilityCriteria eligibility = new EligibilityCriteria(
                19, 34, 30_000_000L, 60_000_000L, 120,
                List.of("서울", "경기"),
                Collections.emptyList(),
                Collections.emptyList()
        );
        return Policy.create(title, category, eligibility);
    }

    @Test
    @DisplayName("정책_저장_및_조회")
    void 정책_저장_및_조회() {
        Policy policy = createPolicy("청년 취업 지원금", PolicyCategory.EMPLOYMENT);

        Policy saved = jpaPolicyRepository.save(policy);

        assertThat(saved).isNotNull();
        assertThat(saved.getId()).isNotNull();
        assertThat(saved.getTitle()).isEqualTo("청년 취업 지원금");
        assertThat(saved.getCategory()).isEqualTo(PolicyCategory.EMPLOYMENT);

        Optional<Policy> found = jpaPolicyRepository.findById(saved.getId());
        assertThat(found).isPresent();
        assertThat(found.get().getTitle()).isEqualTo("청년 취업 지원금");
    }

    @Test
    @DisplayName("카테고리_필터링")
    void 카테고리_필터링() {
        jpaPolicyRepository.save(createPolicy("취업 지원", PolicyCategory.EMPLOYMENT));
        jpaPolicyRepository.save(createPolicy("주거 지원", PolicyCategory.HOUSING));
        jpaPolicyRepository.save(createPolicy("창업 지원", PolicyCategory.STARTUP));

        Page<Policy> employment = jpaPolicyRepository.findByCategory(
                PolicyCategory.EMPLOYMENT, PageRequest.of(0, 10));

        assertThat(employment.getContent()).hasSize(1);
        assertThat(employment.getContent().get(0).getCategory()).isEqualTo(PolicyCategory.EMPLOYMENT);
    }

    @Test
    @DisplayName("활성_정책_조회")
    void 활성_정책_조회() {
        jpaPolicyRepository.save(createPolicy("활성 정책 1", PolicyCategory.EMPLOYMENT));
        jpaPolicyRepository.save(createPolicy("활성 정책 2", PolicyCategory.HOUSING));

        Policy inactivePolicy = createPolicy("비활성 정책", PolicyCategory.FINANCE);
        inactivePolicy.deactivate();
        jpaPolicyRepository.save(inactivePolicy);

        List<Policy> activePolicies = jpaPolicyRepository.findByIsActiveTrue();

        assertThat(activePolicies).hasSize(2);
        assertThat(activePolicies).allMatch(Policy::isActive);
    }

    @Test
    @DisplayName("키워드_검색")
    void 키워드_검색() {
        jpaPolicyRepository.save(createPolicy("청년 취업 지원금", PolicyCategory.EMPLOYMENT));
        jpaPolicyRepository.save(createPolicy("청년 주거 지원", PolicyCategory.HOUSING));
        jpaPolicyRepository.save(createPolicy("창업 육성 프로그램", PolicyCategory.STARTUP));

        Page<PolicyJpaEntity> result = springDataPolicyRepository
                .findByTitleContainingIgnoreCase("청년", PageRequest.of(0, 10));

        assertThat(result.getContent()).hasSize(2);
    }
}
