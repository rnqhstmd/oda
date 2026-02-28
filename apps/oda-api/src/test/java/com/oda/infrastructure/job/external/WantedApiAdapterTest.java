package com.oda.infrastructure.job.external;

import com.github.tomakehurst.wiremock.WireMockServer;
import com.github.tomakehurst.wiremock.core.WireMockConfiguration;
import com.oda.domain.job.JobPosting;
import com.oda.domain.job.JobSource;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.web.client.RestClient;

import java.util.List;

import static com.github.tomakehurst.wiremock.client.WireMock.*;
import static org.assertj.core.api.Assertions.assertThat;

class WantedApiAdapterTest {

    private WireMockServer wireMockServer;
    private WantedApiAdapter adapter;

    @BeforeEach
    void setUp() {
        wireMockServer = new WireMockServer(WireMockConfiguration.wireMockConfig().dynamicPort());
        wireMockServer.start();

        RestClient.Builder builder = RestClient.builder();
        adapter = new WantedApiAdapter(builder, "test-wanted-key",
                "http://localhost:" + wireMockServer.port());
    }

    @AfterEach
    void tearDown() {
        wireMockServer.stop();
    }

    @Test
    @DisplayName("원티드 채용정보 조회 성공")
    void 원티드_채용정보_조회_성공() {
        // given
        wireMockServer.stubFor(get(urlPathEqualTo("/api/v4/jobs"))
                .willReturn(aResponse()
                        .withStatus(200)
                        .withHeader("Content-Type", "application/json")
                        .withBody("""
                                {
                                  "data": [
                                    {
                                      "id": 1001,
                                      "title": "백엔드 엔지니어",
                                      "url": "https://wanted.co.kr/wd/1001",
                                      "company": {
                                        "name": "원티드컴퍼니",
                                        "industry_name": "IT/인터넷",
                                        "type": "중견기업"
                                      },
                                      "address": {
                                        "country": "kr",
                                        "location": "서울시 강남구"
                                      },
                                      "experience_min": 2,
                                      "experience_max": 5,
                                      "skills": ["Java", "Spring Boot", "MySQL"],
                                      "job_type": "정규직",
                                      "detail": {
                                        "intro": "우리는 좋은 회사입니다.",
                                        "main_tasks": "백엔드 서버 개발",
                                        "requirements": "Java 경험 2년 이상",
                                        "preferred_points": "MSA 경험자 우대"
                                      }
                                    },
                                    {
                                      "id": 1002,
                                      "title": "프론트엔드 개발자",
                                      "url": "https://wanted.co.kr/wd/1002",
                                      "company": {
                                        "name": "스타트업B",
                                        "industry_name": "IT/인터넷",
                                        "type": "스타트업"
                                      },
                                      "address": {
                                        "country": "kr",
                                        "location": "서울시 마포구"
                                      },
                                      "experience_min": 1,
                                      "experience_max": 3,
                                      "skills": ["React", "TypeScript"],
                                      "job_type": "정규직"
                                    }
                                  ],
                                  "links": {"next": null}
                                }
                                """)));

        // when
        List<JobPosting> jobs = adapter.fetchJobs();

        // then
        assertThat(jobs).hasSize(2);
        assertThat(jobs.get(0).getTitle()).isEqualTo("백엔드 엔지니어");
        assertThat(jobs.get(0).getSource()).isEqualTo(JobSource.WANTED);
        assertThat(jobs.get(0).getCompany().name()).isEqualTo("원티드컴퍼니");
        assertThat(jobs.get(0).getRequiredSkills()).hasSize(3);
    }

    @Test
    @DisplayName("API 오류 시 빈 목록 반환")
    void API_오류_시_빈_목록_반환() {
        // given
        wireMockServer.stubFor(get(urlPathEqualTo("/api/v4/jobs"))
                .willReturn(aResponse()
                        .withStatus(503)
                        .withBody("Service Unavailable")));

        // when
        List<JobPosting> jobs = adapter.fetchJobs();

        // then
        assertThat(jobs).isEmpty();
    }

    @Test
    @DisplayName("빈 데이터 응답 처리")
    void 빈_데이터_응답_처리() {
        // given
        wireMockServer.stubFor(get(urlPathEqualTo("/api/v4/jobs"))
                .willReturn(aResponse()
                        .withStatus(200)
                        .withHeader("Content-Type", "application/json")
                        .withBody("{\"data\": [], \"links\": {\"next\": null}}")));

        // when
        List<JobPosting> jobs = adapter.fetchJobs();

        // then
        assertThat(jobs).isEmpty();
    }

    @Test
    @DisplayName("스킬 목록이 없는 채용공고 처리")
    void 스킬_목록이_없는_채용공고_처리() {
        // given
        wireMockServer.stubFor(get(urlPathEqualTo("/api/v4/jobs"))
                .willReturn(aResponse()
                        .withStatus(200)
                        .withHeader("Content-Type", "application/json")
                        .withBody("""
                                {
                                  "data": [
                                    {
                                      "id": 2001,
                                      "title": "일반 사무직",
                                      "url": "https://wanted.co.kr/wd/2001",
                                      "company": {"name": "일반회사", "industry_name": "서비스"},
                                      "address": {"country": "kr", "location": "서울"},
                                      "experience_min": 0,
                                      "experience_max": 0,
                                      "job_type": "정규직"
                                    }
                                  ],
                                  "links": {"next": null}
                                }
                                """)));

        // when
        List<JobPosting> jobs = adapter.fetchJobs();

        // then
        assertThat(jobs).hasSize(1);
        assertThat(jobs.get(0).getRequiredSkills()).isEmpty();
    }
}
