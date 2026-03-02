package com.oda.application.policy;

import com.oda.application.policy.dto.PolicyMatchResult;
import com.oda.domain.policy.EligibilityCriteria;
import com.oda.domain.policy.Policy;
import com.oda.domain.policy.PolicyCategory;
import com.oda.domain.policy.PolicyRepository;
import com.oda.domain.user.EmploymentStatus;
import com.oda.domain.user.IncomeInfo;
import com.oda.domain.user.UserProfile;
import com.oda.domain.user.UserProfileRepository;
import com.oda.support.error.CoreException;
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
import static org.mockito.BDDMockito.given;

@ExtendWith(MockitoExtension.class)
class MatchPoliciesServiceTest {

    @Mock
    private PolicyRepository policyRepository;

    @Mock
    private UserProfileRepository userProfileRepository;

    @InjectMocks
    private MatchPoliciesService matchPoliciesService;

    private UserProfile createProfile(Long userId, int birthYear, String sido, EmploymentStatus status, Long personalIncome) {
        UserProfile profile = UserProfile.create(userId);
        profile.update(
                LocalDate.of(birthYear, 1, 1),
                sido, null,
                status,
                List.of(), List.of(), List.of(), List.of(), List.of()
        );
        if (personalIncome != null) {
            profile.updateIncomeInfo(new IncomeInfo(personalIncome, null, null));
        }
        return profile;
    }

    private Policy createPolicy(int minAge, int maxAge, Long maxIncome, String... regions) {
        List<String> regionList = regions.length > 0 ? List.of(regions) : null;
        EligibilityCriteria criteria = new EligibilityCriteria(
                minAge, maxAge, maxIncome, null, null, regionList, null, null);
        return Policy.create("테스트 정책", PolicyCategory.EMPLOYMENT, criteria);
    }

    @Test
    void 활성_정책_중_조건_맞는_정책만_반환() {
        // given
        Long userId = 1L;
        UserProfile profile = createProfile(userId, 1995, "서울", EmploymentStatus.UNEMPLOYED, 1_500_000L);
        given(userProfileRepository.findByUserId(userId)).willReturn(Optional.of(profile));

        Policy matching = createPolicy(19, 34, 2_000_000L, "서울");
        Policy nonMatching = createPolicy(19, 34, 2_000_000L, "부산");
        given(policyRepository.findByIsActiveTrue()).willReturn(List.of(matching, nonMatching));

        // when
        List<PolicyMatchResult> results = matchPoliciesService.matchPolicies(userId);

        // then
        assertThat(results).hasSize(1);
        assertThat(results.get(0).policy()).isEqualTo(matching);
    }

    @Test
    void 조건에_맞는_정책_없으면_빈_리스트() {
        // given
        Long userId = 2L;
        UserProfile profile = createProfile(userId, 1970, "제주", EmploymentStatus.EMPLOYED, 5_000_000L);
        given(userProfileRepository.findByUserId(userId)).willReturn(Optional.of(profile));

        Policy policy = createPolicy(19, 34, 2_000_000L, "서울");
        given(policyRepository.findByIsActiveTrue()).willReturn(List.of(policy));

        // when
        List<PolicyMatchResult> results = matchPoliciesService.matchPolicies(userId);

        // then
        assertThat(results).isEmpty();
    }

    @Test
    void 활성_정책_없으면_빈_리스트() {
        // given
        Long userId = 3L;
        UserProfile profile = createProfile(userId, 1995, "서울", EmploymentStatus.UNEMPLOYED, null);
        given(userProfileRepository.findByUserId(userId)).willReturn(Optional.of(profile));
        given(policyRepository.findByIsActiveTrue()).willReturn(List.of());

        // when
        List<PolicyMatchResult> results = matchPoliciesService.matchPolicies(userId);

        // then
        assertThat(results).isEmpty();
    }

    @Test
    void 사용자_프로필_없으면_예외() {
        // given
        Long userId = 99L;
        given(userProfileRepository.findByUserId(userId)).willReturn(Optional.empty());

        // when & then
        assertThatThrownBy(() -> matchPoliciesService.matchPolicies(userId))
                .isInstanceOf(CoreException.class)
                .hasMessageContaining("UserProfile not found");
    }

    @Test
    void 여러_조건_충족하는_정책_모두_반환() {
        // given
        Long userId = 4L;
        UserProfile profile = createProfile(userId, 2000, "경기", EmploymentStatus.STUDENT, 500_000L);
        given(userProfileRepository.findByUserId(userId)).willReturn(Optional.of(profile));

        Policy policy1 = createPolicy(19, 34, 2_000_000L, "경기");
        Policy policy2 = createPolicy(18, 29, 1_000_000L, "서울", "경기");
        given(policyRepository.findByIsActiveTrue()).willReturn(List.of(policy1, policy2));

        // when
        List<PolicyMatchResult> results = matchPoliciesService.matchPolicies(userId);

        // then
        assertThat(results).hasSize(2);
    }
}
