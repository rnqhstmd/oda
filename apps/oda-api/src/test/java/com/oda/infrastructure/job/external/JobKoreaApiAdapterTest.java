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

class JobKoreaApiAdapterTest {

    private WireMockServer wireMockServer;
    private JobKoreaApiAdapter adapter;

    @BeforeEach
    void setUp() {
        wireMockServer = new WireMockServer(WireMockConfiguration.wireMockConfig().dynamicPort());
        wireMockServer.start();

        RestClient.Builder builder = RestClient.builder();
        adapter = new JobKoreaApiAdapter(builder,
                "http://localhost:" + wireMockServer.port());
    }

    @AfterEach
    void tearDown() {
        wireMockServer.stop();
    }

    @Test
    @DisplayName("잡코리아 채용정보 조회 성공 및 리다이렉트 URL 포함")
    void 잡코리아_채용정보_조회_성공_및_리다이렉트_URL_포함() {
        // given
        wireMockServer.stubFor(get(urlPathEqualTo("/api/jobs"))
                .willReturn(aResponse()
                        .withStatus(200)
                        .withHeader("Content-Type", "application/json")
                        .withBody("""
                                {
                                  "result": "success",
                                  "totalCount": 2,
                                  "jobs": [
                                    {
                                      "jobId": "JK-12345",
                                      "jobTitle": "자바 백엔드 개발자",
                                      "companyName": "잡코리아테크",
                                      "companyType": "중견기업",
                                      "workLocation": "서울 강남구",
                                      "salaryInfo": "4000~6000만원",
                                      "experienceLevel": "경력 3년 이상",
                                      "educationLevel": "학사",
                                      "jobType": "정규직",
                                      "skills": ["Java", "Spring", "JPA"],
                                      "deadlineDate": "2025.03.31",
                                      "jobUrl": "https://www.jobkorea.co.kr/Recruit/GI_Read/JK-12345",
                                      "jobDescription": "백엔드 개발자 채용"
                                    },
                                    {
                                      "jobId": "JK-67890",
                                      "jobTitle": "DevOps 엔지니어",
                                      "companyName": "클라우드컴퍼니",
                                      "companyType": "대기업",
                                      "workLocation": "성남시 분당구",
                                      "salaryInfo": "5000~8000만원",
                                      "experienceLevel": "경력 5년 이상",
                                      "educationLevel": "학사",
                                      "jobType": "정규직",
                                      "skills": ["Kubernetes", "Docker", "AWS"],
                                      "deadlineDate": "2025.04.30",
                                      "jobUrl": "https://www.jobkorea.co.kr/Recruit/GI_Read/JK-67890",
                                      "jobDescription": "DevOps 엔지니어 채용"
                                    }
                                  ]
                                }
                                """)));

        // when
        List<JobPosting> jobs = adapter.fetchJobs();

        // then
        assertThat(jobs).hasSize(2);
        assertThat(jobs.get(0).getTitle()).isEqualTo("자바 백엔드 개발자");
        assertThat(jobs.get(0).getSource()).isEqualTo(JobSource.JOBKOREA);
        assertThat(jobs.get(0).getCompany().name()).isEqualTo("잡코리아테크");
        // Verify redirect URL is set to JobKorea job detail page
        assertThat(jobs.get(0).getApplicationUrl()).contains("jobkorea.co.kr/Recruit/GI_Read/JK-12345");
        assertThat(jobs.get(1).getApplicationUrl()).contains("jobkorea.co.kr/Recruit/GI_Read/JK-67890");
    }

    @Test
    @DisplayName("API 오류 시 빈 목록 반환")
    void API_오류_시_빈_목록_반환() {
        // given
        wireMockServer.stubFor(get(urlPathEqualTo("/api/jobs"))
                .willReturn(aResponse()
                        .withStatus(500)
                        .withBody("Internal Server Error")));

        // when
        List<JobPosting> jobs = adapter.fetchJobs();

        // then
        assertThat(jobs).isEmpty();
    }

    @Test
    @DisplayName("빈 채용공고 목록 처리")
    void 빈_채용공고_목록_처리() {
        // given
        wireMockServer.stubFor(get(urlPathEqualTo("/api/jobs"))
                .willReturn(aResponse()
                        .withStatus(200)
                        .withHeader("Content-Type", "application/json")
                        .withBody("{\"result\": \"success\", \"totalCount\": 0, \"jobs\": []}")));

        // when
        List<JobPosting> jobs = adapter.fetchJobs();

        // then
        assertThat(jobs).isEmpty();
    }
}
