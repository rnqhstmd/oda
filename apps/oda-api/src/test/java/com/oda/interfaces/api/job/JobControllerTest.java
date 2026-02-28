package com.oda.interfaces.api.job;

import com.oda.infrastructure.security.SecurityConfig;
import com.oda.infrastructure.security.jwt.JwtAuthenticationFilter;
import com.oda.infrastructure.security.jwt.JwtTokenProvider;
import com.oda.application.job.dto.JobMatchResult;
import com.oda.application.job.dto.JobResult;
import com.oda.application.job.dto.JobSearchQuery;
import com.oda.application.job.GetJobsService;
import com.oda.application.job.MatchJobsService;
import com.oda.application.job.AnalyzeGapService;
import com.oda.domain.job.Company;
import com.oda.domain.job.GapAnalysis;
import com.oda.domain.job.JobPosting;
import com.oda.domain.job.JobSource;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;
import java.util.Collections;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(JobController.class)
@Import({SecurityConfig.class, JwtAuthenticationFilter.class})
class JobControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private GetJobsService getJobsService;

    @MockBean
    private MatchJobsService matchJobsService;

    @MockBean
    private AnalyzeGapService analyzeGapService;

    @MockBean
    private JwtTokenProvider jwtTokenProvider;

    private UsernamePasswordAuthenticationToken authFor(Long userId) {
        return new UsernamePasswordAuthenticationToken(userId, null, Collections.emptyList());
    }

    private static final Company COMPANY = new Company("테스트회사", "IT", "중견", "서울");

    @Test
    void 인증_없이_matched_접근시_401() throws Exception {
        mockMvc.perform(get("/api/v1/jobs/matched"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void 공고_목록_조회_성공() throws Exception {
        JobResult result = new JobResult(1L, "백엔드 개발자", "테스트회사", "서울", "5000만원",
                JobSource.SARAMIN, LocalDate.now().plusDays(30));

        given(getJobsService.getJobs(any(JobSearchQuery.class)))
                .willReturn(new PageImpl<>(List.of(result), PageRequest.of(0, 20), 1));

        mockMvc.perform(get("/api/v1/jobs"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.meta.result").value("SUCCESS"))
                .andExpect(jsonPath("$.data.content[0].title").value("백엔드 개발자"))
                .andExpect(jsonPath("$.data.content[0].companyName").value("테스트회사"));
    }

    @Test
    void 공고_상세_조회_성공() throws Exception {
        Long userId = 1L;
        JobPosting job = JobPosting.create("백엔드 개발자", COMPANY, JobSource.SARAMIN);
        ReflectionTestUtils.setField(job, "id", 1L);

        given(getJobsService.getJob(1L)).willReturn(job);

        mockMvc.perform(get("/api/v1/jobs/1")
                        .with(SecurityMockMvcRequestPostProcessors.authentication(authFor(userId))))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.meta.result").value("SUCCESS"))
                .andExpect(jsonPath("$.data.title").value("백엔드 개발자"));
    }

    @Test
    void 매칭_공고_조회_인증_성공() throws Exception {
        Long userId = 1L;
        JobPosting job = JobPosting.create("백엔드 개발자", COMPANY, JobSource.SARAMIN);
        ReflectionTestUtils.setField(job, "id", 1L);

        GapAnalysis gap = new GapAnalysis(List.of("java"), List.of(), 100, List.of());
        JobMatchResult matchResult = new JobMatchResult(job, 85, gap);

        given(matchJobsService.matchJobs(userId)).willReturn(List.of(matchResult));

        mockMvc.perform(get("/api/v1/jobs/matched")
                        .with(SecurityMockMvcRequestPostProcessors.authentication(authFor(userId))))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.meta.result").value("SUCCESS"))
                .andExpect(jsonPath("$.data[0].title").value("백엔드 개발자"));
    }

    @Test
    void 갭분석_인증_성공() throws Exception {
        Long userId = 1L;
        Long jobId = 1L;
        GapAnalysis gap = new GapAnalysis(List.of("java"), List.of("spring"), 50,
                List.of("spring 스킬을 학습하세요."));

        given(analyzeGapService.analyzeGap(jobId, userId)).willReturn(gap);

        mockMvc.perform(get("/api/v1/jobs/1/gap")
                        .with(SecurityMockMvcRequestPostProcessors.authentication(authFor(userId))))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.meta.result").value("SUCCESS"))
                .andExpect(jsonPath("$.data.matchPercentage").value(50))
                .andExpect(jsonPath("$.data.matchedSkills[0]").value("java"));
    }

    @Test
    void 갭분석_인증_없으면_401() throws Exception {
        mockMvc.perform(get("/api/v1/jobs/1/gap"))
                .andExpect(status().isUnauthorized());
    }
}
