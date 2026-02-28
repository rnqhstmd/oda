package com.oda.application.job;

import com.oda.support.error.CoreException;
import com.oda.support.error.ErrorType;
import com.oda.domain.job.GapAnalysis;
import com.oda.domain.job.JobPosting;
import com.oda.domain.job.JobPostingRepository;
import com.oda.domain.job.JobMatchingSpec;
import com.oda.domain.user.UserProfile;
import com.oda.domain.user.WorkExperience;
import com.oda.domain.user.UserProfileRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.Period;
import java.util.Collections;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class AnalyzeGapService {

    private final JobPostingRepository jobPostingRepository;
    private final UserProfileRepository userProfileRepository;

    public GapAnalysis analyzeGap(Long jobId, Long userId) {
        JobPosting job = jobPostingRepository.findById(jobId)
                .orElseThrow(() -> new CoreException(ErrorType.NOT_FOUND, "Job not found with id: " + jobId));

        UserProfile profile = userProfileRepository.findByUserId(userId)
                .orElse(UserProfile.create(userId));

        List<String> userSkills = profile.getSkills() != null ? profile.getSkills() : Collections.emptyList();
        int experienceYears = calculateTotalExperienceYears(profile);

        return JobMatchingSpec.analyzeGap(job, userSkills, experienceYears);
    }

    private int calculateTotalExperienceYears(UserProfile profile) {
        if (profile.getWorkExperiences() == null) return 0;
        return profile.getWorkExperiences().stream()
                .mapToInt(this::getDurationYears)
                .sum();
    }

    private int getDurationYears(WorkExperience we) {
        LocalDate start = we.startDate();
        if (start == null) return 0;
        LocalDate end = we.isCurrent() || we.endDate() == null ? LocalDate.now() : we.endDate();
        return Math.max(0, Period.between(start, end).getYears());
    }
}
