package com.oda.infrastructure.job.external;

import com.github.tomakehurst.wiremock.WireMockServer;
import com.github.tomakehurst.wiremock.core.WireMockConfiguration;
import com.oda.support.error.CoreException;
import com.oda.support.error.ErrorType;
import com.oda.domain.job.JobPosting;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.web.client.RestClient;

import java.util.List;

import static com.github.tomakehurst.wiremock.client.WireMock.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class SaraminApiAdapterTest {

    private WireMockServer wireMockServer;
    private SaraminApiAdapter adapter;

    @BeforeEach
    void setUp() {
        wireMockServer = new WireMockServer(WireMockConfiguration.wireMockConfig().dynamicPort());
        wireMockServer.start();

        RestClient.Builder builder = RestClient.builder();
        adapter = new SaraminApiAdapter(builder, "test-api-key",
                "http://localhost:" + wireMockServer.port());
    }

    @AfterEach
    void tearDown() {
        wireMockServer.stop();
    }

    @Test
    @DisplayName("채용정보 조회 성공")
    void 채용정보_조회_성공() {
        // given
        wireMockServer.stubFor(get(urlPathEqualTo("/job-search"))
                .willReturn(aResponse()
                        .withStatus(200)
                        .withHeader("Content-Type", "application/json")
                        .withBody("""
                                {
                                  "jobs": {
                                    "job": [
                                      {
                                        "id": "SAR-001",
                                        "url": "https://saramin.co.kr/job/1",
                                        "active": 1,
                                        "position": {
                                          "title": "백엔드 개발자",
                                          "location": {"name": "서울"},
                                          "job-type": {"name": "정규직"},
                                          "experience-level": {"min": 3, "name": "경력3년"},
                                          "required-education-level": {"name": "학사"},
                                          "job-mid-code": {"name": "Java"}
                                        },
                                        "company": {
                                          "detail": {
                                            "name": "테스트컴퍼니",
                                            "industry": "IT",
                                            "size": "중견기업",
                                            "location": "서울"
                                          }
                                        },
                                        "salary": {"name": "4000~5000만원"}
                                      },
                                      {
                                        "id": "SAR-002",
                                        "url": "https://saramin.co.kr/job/2",
                                        "active": 1,
                                        "position": {
                                          "title": "프론트엔드 개발자",
                                          "location": {"name": "판교"},
                                          "job-type": {"name": "정규직"},
                                          "experience-level": {"min": 2, "name": "경력2년"},
                                          "required-education-level": {"name": "학사"}
                                        },
                                        "company": {
                                          "detail": {
                                            "name": "스타트업A",
                                            "industry": "IT",
                                            "size": "스타트업",
                                            "location": "판교"
                                          }
                                        },
                                        "salary": {"name": "3500~4500만원"}
                                      }
                                    ]
                                  }
                                }
                                """)));

        // when
        List<JobPosting> jobs = adapter.fetchJobs();

        // then
        assertThat(jobs).hasSize(2);
        assertThat(jobs.get(0).getTitle()).isEqualTo("백엔드 개발자");
        assertThat(jobs.get(0).getCompany().name()).isEqualTo("테스트컴퍼니");
        assertThat(jobs.get(1).getTitle()).isEqualTo("프론트엔드 개발자");
    }

    @Test
    @DisplayName("일일 호출 한도 초과 예외")
    void 일일_호출한도_초과_예외() {
        // given - call MAX_DAILY_CALLS times to exhaust the limit
        wireMockServer.stubFor(get(urlPathEqualTo("/job-search"))
                .willReturn(aResponse()
                        .withStatus(200)
                        .withHeader("Content-Type", "application/json")
                        .withBody("{\"jobs\": {\"job\": []}}")));

        // exhaust 480 calls
        for (int i = 0; i < 480; i++) {
            adapter.fetchJobs();
        }

        // when & then - 481st call should throw
        assertThatThrownBy(() -> adapter.fetchJobs())
                .isInstanceOf(CoreException.class)
                .hasMessageContaining("Saramin daily limit exceeded")
                .satisfies(e -> org.assertj.core.api.Assertions.assertThat(((CoreException) e).getErrorType()).isEqualTo(ErrorType.BAD_REQUEST));
    }

    @Test
    @DisplayName("API 오류 응답 처리 - 빈 목록 반환")
    void API_오류_응답_처리() {
        // given
        wireMockServer.stubFor(get(urlPathEqualTo("/job-search"))
                .willReturn(aResponse()
                        .withStatus(500)
                        .withHeader("Content-Type", "application/json")
                        .withBody("{\"error\": \"Internal Server Error\"}")));

        // when
        List<JobPosting> jobs = adapter.fetchJobs();

        // then - should return empty list on error, not throw
        assertThat(jobs).isEmpty();
    }

    @Test
    @DisplayName("빈 응답 처리")
    void 빈_응답_처리() {
        // given
        wireMockServer.stubFor(get(urlPathEqualTo("/job-search"))
                .willReturn(aResponse()
                        .withStatus(200)
                        .withHeader("Content-Type", "application/json")
                        .withBody("{\"jobs\": {\"job\": []}}")));

        // when
        List<JobPosting> jobs = adapter.fetchJobs();

        // then
        assertThat(jobs).isEmpty();
    }

    @Test
    @DisplayName("일일 카운터 리셋 후 정상 호출")
    void 일일_카운터_리셋_후_정상_호출() {
        // given
        wireMockServer.stubFor(get(urlPathEqualTo("/job-search"))
                .willReturn(aResponse()
                        .withStatus(200)
                        .withHeader("Content-Type", "application/json")
                        .withBody("{\"jobs\": {\"job\": []}}")));

        // exhaust 480 calls
        for (int i = 0; i < 480; i++) {
            adapter.fetchJobs();
        }

        // reset
        adapter.resetDailyCount();

        // when - should succeed again
        List<JobPosting> jobs = adapter.fetchJobs();

        // then
        assertThat(jobs).isEmpty();
        assertThat(adapter.getDailyCallCount()).isEqualTo(1);
    }
}
