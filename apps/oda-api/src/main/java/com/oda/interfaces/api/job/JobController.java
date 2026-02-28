package com.oda.interfaces.api.job;

import com.oda.interfaces.api.ApiResponse;
import com.oda.interfaces.api.job.dto.GapAnalysisResponse;
import com.oda.interfaces.api.job.dto.JobDetailResponse;
import com.oda.interfaces.api.job.dto.JobResponse;
import com.oda.application.job.dto.JobMatchResult;
import com.oda.application.job.dto.JobResult;
import com.oda.application.job.dto.JobSearchQuery;
import com.oda.application.job.GetJobsService;
import com.oda.application.job.MatchJobsService;
import com.oda.application.job.AnalyzeGapService;
import com.oda.domain.job.GapAnalysis;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/v1/jobs")
@RequiredArgsConstructor
public class JobController {

    private final GetJobsService getJobsService;
    private final MatchJobsService matchJobsService;
    private final AnalyzeGapService analyzeGapService;

    @GetMapping
    public ResponseEntity<ApiResponse<Page<JobResponse>>> getJobs(
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) String location,
            @PageableDefault(size = 20) Pageable pageable) {
        JobSearchQuery query = new JobSearchQuery(keyword, location, null, pageable);
        Page<JobResult> results = getJobsService.getJobs(query);
        Page<JobResponse> response = results.map(JobResponse::from);
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<JobDetailResponse>> getJob(@PathVariable Long id) {
        JobDetailResponse response = JobDetailResponse.from(getJobsService.getJob(id));
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    @GetMapping("/matched")
    public ResponseEntity<ApiResponse<List<JobResponse>>> matchJobs(
            @AuthenticationPrincipal Long userId) {
        List<JobMatchResult> results = matchJobsService.matchJobs(userId);
        List<JobResponse> response = results.stream()
                .map(r -> JobResponse.from(JobResult.from(r.job())))
                .collect(Collectors.toList());
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    @GetMapping("/{id}/gap")
    public ResponseEntity<ApiResponse<GapAnalysisResponse>> analyzeGap(
            @PathVariable Long id,
            @AuthenticationPrincipal Long userId) {
        GapAnalysis gap = analyzeGapService.analyzeGap(id, userId);
        return ResponseEntity.ok(ApiResponse.success(GapAnalysisResponse.from(gap)));
    }
}
