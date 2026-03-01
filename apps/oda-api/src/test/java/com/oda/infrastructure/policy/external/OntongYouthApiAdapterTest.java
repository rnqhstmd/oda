package com.oda.infrastructure.policy.external;

import com.github.tomakehurst.wiremock.junit5.WireMockRuntimeInfo;
import com.github.tomakehurst.wiremock.junit5.WireMockTest;
import com.oda.domain.policy.Policy;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.web.client.RestClient;

import java.util.List;

import static com.github.tomakehurst.wiremock.client.WireMock.*;
import static org.assertj.core.api.Assertions.assertThat;

@WireMockTest(httpPort = 8090)
class OntongYouthApiAdapterTest {

    private OntongYouthApiAdapter adapter;

    @BeforeEach
    void setUp(WireMockRuntimeInfo wireMock) {
        RestClient restClient = RestClient.builder()
                .baseUrl("http://localhost:" + wireMock.getHttpPort())
                .build();
        adapter = new OntongYouthApiAdapter(restClient);
    }

    @Test
    @DisplayName("정책_목록_조회_성공")
    void 정책_목록_조회_성공() {
        stubFor(get(urlPathMatching("/opi/empBizInfo.do"))
                .willReturn(aResponse()
                        .withStatus(200)
                        .withHeader("Content-Type", "application/json")
                        .withBody("""
                                {
                                  "result": "success",
                                  "totalCount": 2,
                                  "data": [
                                    {
                                      "bizId": "B2024001",
                                      "polyBizSjnm": "청년 창업 지원",
                                      "polyItcnCn": "창업을 준비하는 청년을 위한 지원 사업",
                                      "sporCn": "창업 자금 지원",
                                      "polyRlmCd": "023050",
                                      "cnsgNmor": "중소벤처기업부",
                                      "ageMin": 19,
                                      "ageMax": 39,
                                      "acptUrl": "https://example.com/startup",
                                      "rgnNm": "전국"
                                    },
                                    {
                                      "bizId": "B2024002",
                                      "polyBizSjnm": "청년 금융 지원",
                                      "polyItcnCn": "저금리 대출 지원",
                                      "sporCn": "금융 상세",
                                      "polyRlmCd": "023030",
                                      "cnsgNmor": "금융위원회",
                                      "ageMin": 19,
                                      "ageMax": 34,
                                      "acptUrl": "https://example.com/finance",
                                      "rgnNm": "서울"
                                    }
                                  ]
                                }
                                """)));

        List<Policy> policies = adapter.fetchPolicies();

        assertThat(policies).hasSize(2);
        assertThat(policies.get(0).getTitle()).isEqualTo("청년 창업 지원");
        assertThat(policies.get(1).getTitle()).isEqualTo("청년 금융 지원");
    }

    @Test
    @DisplayName("API_오류_응답_처리")
    void API_오류_응답_처리() {
        stubFor(get(urlPathMatching("/opi/empBizInfo.do"))
                .willReturn(aResponse()
                        .withStatus(503)
                        .withBody("Service Unavailable")));

        List<Policy> policies = adapter.fetchPolicies();

        assertThat(policies).isEmpty();
    }
}
