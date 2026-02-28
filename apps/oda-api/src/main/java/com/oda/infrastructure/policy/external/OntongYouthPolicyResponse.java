package com.oda.infrastructure.policy.external;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public record OntongYouthPolicyResponse(
        @JsonProperty("result") String result,
        @JsonProperty("data") List<PolicyItem> data,
        @JsonProperty("totalCount") Integer totalCount
) {
    @JsonIgnoreProperties(ignoreUnknown = true)
    public record PolicyItem(
            @JsonProperty("bizId") String policyId,
            @JsonProperty("polyBizSjnm") String title,
            @JsonProperty("polyItcnCn") String summary,
            @JsonProperty("sporCn") String description,
            @JsonProperty("polyRlmCd") String categoryCode,
            @JsonProperty("cnsgNmor") String organizationName,
            @JsonProperty("ageMin") Integer minAge,
            @JsonProperty("ageMax") Integer maxAge,
            @JsonProperty("acptUrl") String applicationUrl,
            @JsonProperty("rgnNm") String regionName
    ) {}
}
