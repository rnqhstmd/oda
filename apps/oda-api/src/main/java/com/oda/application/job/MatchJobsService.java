package com.oda.application.job;

import com.oda.application.job.dto.JobMatchResult;
import com.oda.domain.job.GapAnalysis;
import com.oda.domain.job.JobPosting;
import com.oda.domain.job.JobPostingRepository;
import com.oda.domain.job.JobMatchingSpec;
import com.oda.domain.user.UserProfile;
import com.oda.domain.user.UserProfileRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class MatchJobsService {

    private final JobPostingRepository jobPostingRepository;
    private final UserProfileRepository userProfileRepository;

    public List<JobMatchResult> matchJobs(Long userId) {
        UserProfile profile = userProfileRepository.findByUserId(userId)
                .orElse(UserProfile.create(userId));

        List<String> userSkills = profile.getSkills() != null ? profile.getSkills() : Collections.emptyList();
        int experienceYears = profile.calculateTotalExperienceYears();
        String education = profile.resolveHighestEducation();

        List<JobPosting> activeJobs = jobPostingRepository.findByActiveTrue();

        return activeJobs.stream()
                .map(job -> {
                    int score = JobMatchingSpec.calculateMatchScore(job, userSkills, experienceYears, education);
                    GapAnalysis gap = JobMatchingSpec.analyzeGap(job, userSkills, experienceYears);
                    return new JobMatchResult(job, score, gap);
                })
                .filter(result -> result.matchScore() > 0)
                .sorted(Comparator.comparingInt(JobMatchResult::matchScore).reversed())
                .collect(Collectors.toList());
    }

}
