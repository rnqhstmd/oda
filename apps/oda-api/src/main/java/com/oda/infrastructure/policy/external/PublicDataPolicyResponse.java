package com.oda.infrastructure.policy.external;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public record PublicDataPolicyResponse(
        @JsonProperty("response") Response response
) {
    @JsonIgnoreProperties(ignoreUnknown = true)
    public record Response(
            @JsonProperty("header") Header header,
            @JsonProperty("body") Body body
    ) {}

    @JsonIgnoreProperties(ignoreUnknown = true)
    public record Header(
            @JsonProperty("resultCode") String resultCode,
            @JsonProperty("resultMsg") String resultMsg
    ) {}

    @JsonIgnoreProperties(ignoreUnknown = true)
    public record Body(
            @JsonProperty("items") Items items,
            @JsonProperty("totalCount") Integer totalCount,
            @JsonProperty("pageNo") Integer pageNo,
            @JsonProperty("numOfRows") Integer numOfRows
    ) {}

    @JsonIgnoreProperties(ignoreUnknown = true)
    public record Items(
            @JsonProperty("item") List<Item> item
    ) {}

    @JsonIgnoreProperties(ignoreUnknown = true)
    public record Item(
            @JsonProperty("polyBizSecd") String policyId,
            @JsonProperty("polyBizSjnm") String title,
            @JsonProperty("polyItcnCn") String summary,
            @JsonProperty("sporCn") String description,
            @JsonProperty("polyRlmCd") String categoryCode,
            @JsonProperty("cnsgNmor") String organizationName,
            @JsonProperty("ageInfo") String ageInfo,
            @JsonProperty("incmeCnd") String incomeCriteria,
            @JsonProperty("acptMthd") String applicationMethod,
            @JsonProperty("rqutPrdCn") String applicationPeriod,
            @JsonProperty("polyUrl") String applicationUrl,
            @JsonProperty("rgnNm") String regionName,
            @JsonProperty("empmSttsCd") String employmentStatusCode
    ) {}
}
