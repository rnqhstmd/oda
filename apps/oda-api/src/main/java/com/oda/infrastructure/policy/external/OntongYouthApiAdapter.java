package com.oda.infrastructure.policy.external;

import com.oda.domain.policy.EligibilityCriteria;
import com.oda.domain.policy.Policy;
import com.oda.domain.policy.PolicyCategory;
import com.oda.domain.policy.PolicyDataSource;

import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Component("ontongYouthApiAdapter")
public class OntongYouthApiAdapter implements PolicyDataSource {

    private static final String DEFAULT_BASE_URL = "https://www.youthcenter.go.kr";
    private static final Map<String, PolicyCategory> CATEGORY_MAP = Map.of(
            "023010", PolicyCategory.EMPLOYMENT,
            "023020", PolicyCategory.HOUSING,
            "023030", PolicyCategory.FINANCE,
            "023040", PolicyCategory.EDUCATION,
            "023050", PolicyCategory.STARTUP
    );

    private final RestClient restClient;

    public OntongYouthApiAdapter() {
        this.restClient = RestClient.builder()
                .baseUrl(DEFAULT_BASE_URL)
                .build();
    }

    // Package-private constructor for testing with WireMock
    OntongYouthApiAdapter(RestClient restClient) {
        this.restClient = restClient;
    }

    @Override
    public List<Policy> fetchPolicies() {
        try {
            OntongYouthPolicyResponse response = restClient.get()
                    .uri(uriBuilder -> uriBuilder
                            .path("/opi/empBizInfo.do")
                            .queryParam("pageIndex", 1)
                            .queryParam("display", 100)
                            .build())
                    .retrieve()
                    .body(OntongYouthPolicyResponse.class);

            if (response == null || response.data() == null) {
                return Collections.emptyList();
            }

            return response.data().stream()
                    .map(this::toPolicy)
                    .collect(Collectors.toList());
        } catch (Exception e) {
            return Collections.emptyList();
        }
    }

    private Policy toPolicy(OntongYouthPolicyResponse.PolicyItem item) {
        PolicyCategory category = CATEGORY_MAP.getOrDefault(item.categoryCode(), PolicyCategory.EMPLOYMENT);

        EligibilityCriteria eligibility = new EligibilityCriteria(
                item.minAge(), item.maxAge(),
                null, null, null,
                item.regionName() != null ? List.of(item.regionName()) : Collections.emptyList(),
                Collections.emptyList(),
                Collections.emptyList()
        );

        Policy policy = Policy.create(
                item.title() != null ? item.title() : "Unknown",
                category,
                eligibility
        );
        policy.update(
                item.title(),
                item.summary(),
                item.description(),
                item.organizationName(),
                eligibility,
                null, null,
                item.applicationUrl()
        );
        policy.markSynced(LocalDateTime.now());
        return policy;
    }
}
