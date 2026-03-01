package com.oda.infrastructure.policy.external;

import com.github.tomakehurst.wiremock.client.WireMock;
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

@WireMockTest(httpPort = 8089)
class PublicDataApiAdapterTest {

    private PublicDataApiAdapter adapter;

    @BeforeEach
    void setUp(WireMockRuntimeInfo wireMock) {
        RestClient restClient = RestClient.builder()
                .baseUrl("http://localhost:" + wireMock.getHttpPort())
                .build();
        adapter = new PublicDataApiAdapter(restClient, "test-api-key");
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
                                  "response": {
                                    "header": {
                                      "resultCode": "00",
                                      "resultMsg": "NORMAL SERVICE."
                                    },
                                    "body": {
                                      "items": {
                                        "item": [
                                          {
                                            "polyBizSecd": "R2024010001",
                                            "polyBizSjnm": "청년 취업 지원금",
                                            "polyItcnCn": "청년 취업을 지원하는 사업입니다",
                                            "sporCn": "상세 내용",
                                            "polyRlmCd": "023010",
                                            "cnsgNmor": "고용노동부",
                                            "ageInfo": "19~34세",
                                            "polyUrl": "https://example.com/apply",
                                            "rgnNm": "서울"
                                          },
                                          {
                                            "polyBizSecd": "R2024010002",
                                            "polyBizSjnm": "청년 주거 지원",
                                            "polyItcnCn": "주거비 지원 사업",
                                            "sporCn": "주거 상세",
                                            "polyRlmCd": "023020",
                                            "cnsgNmor": "국토교통부",
                                            "ageInfo": "19~39세",
                                            "polyUrl": "https://example.com/housing",
                                            "rgnNm": "전국"
                                          }
                                        ]
                                      },
                                      "totalCount": 2,
                                      "pageNo": 1,
                                      "numOfRows": 100
                                    }
                                  }
                                }
                                """)));

        List<Policy> policies = adapter.fetchPolicies();

        assertThat(policies).hasSize(2);
        assertThat(policies.get(0).getTitle()).isEqualTo("청년 취업 지원금");
        assertThat(policies.get(1).getTitle()).isEqualTo("청년 주거 지원");
    }

    @Test
    @DisplayName("API_오류_응답_처리")
    void API_오류_응답_처리() {
        stubFor(get(urlPathMatching("/opi/empBizInfo.do"))
                .willReturn(aResponse()
                        .withStatus(500)
                        .withBody("Internal Server Error")));

        List<Policy> policies = adapter.fetchPolicies();

        assertThat(policies).isEmpty();
    }

    @Test
    @DisplayName("빈_결과_처리")
    void 빈_결과_처리() {
        stubFor(get(urlPathMatching("/opi/empBizInfo.do"))
                .willReturn(aResponse()
                        .withStatus(200)
                        .withHeader("Content-Type", "application/json")
                        .withBody("""
                                {
                                  "response": {
                                    "header": {
                                      "resultCode": "00",
                                      "resultMsg": "NORMAL SERVICE."
                                    },
                                    "body": {
                                      "items": {
                                        "item": []
                                      },
                                      "totalCount": 0,
                                      "pageNo": 1,
                                      "numOfRows": 100
                                    }
                                  }
                                }
                                """)));

        List<Policy> policies = adapter.fetchPolicies();

        assertThat(policies).isEmpty();
    }
}
