package com.oda.application.simulation;

import com.oda.domain.job.JobPosting;
import com.oda.domain.job.JobPostingRepository;
import com.oda.domain.simulation.CareerPath;
import com.oda.domain.simulation.CareerStep;
import com.oda.domain.simulation.SimulationResult;
import com.oda.domain.user.Certification;
import com.oda.domain.user.UserProfile;
import com.oda.domain.user.UserProfileRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@Transactional(readOnly = true)
public class CareerSimulationService {

    private final UserProfileRepository userProfileRepository;
    private final JobPostingRepository jobPostingRepository;

    public CareerSimulationService(UserProfileRepository userProfileRepository,
                                   JobPostingRepository jobPostingRepository) {
        this.userProfileRepository = userProfileRepository;
        this.jobPostingRepository = jobPostingRepository;
    }

    public SimulationResult simulate(Long userId, String targetJobCategory) {
        UserProfile profile = userProfileRepository.findByUserId(userId)
                .orElse(null);

        Set<String> userSkills = new HashSet<>();
        Set<String> userCerts = new HashSet<>();
        String currentPosition = "신입";

        if (profile != null) {
            if (profile.getSkills() != null) {
                userSkills.addAll(profile.getSkills());
            }
            if (profile.getCertifications() != null) {
                userCerts.addAll(profile.getCertifications().stream()
                        .map(Certification::name)
                        .collect(Collectors.toSet()));
            }
            if (profile.getWorkExperiences() != null && !profile.getWorkExperiences().isEmpty()) {
                currentPosition = "경력";
            }
        }

        List<JobPosting> targetJobs = jobPostingRepository.findByActiveTrue().stream()
                .filter(j -> j.getTitle() != null &&
                        j.getTitle().toLowerCase().contains(targetJobCategory.toLowerCase()))
                .toList();

        Set<String> requiredSkills = new HashSet<>();
        Set<String> requiredCerts = new HashSet<>();
        for (JobPosting job : targetJobs) {
            if (job.getRequiredSkills() != null) {
                job.getRequiredSkills().forEach(s -> requiredSkills.add(s.skillName()));
            }
            if (job.getRequiredEducation() != null) {
                requiredCerts.add(job.getRequiredEducation());
            }
        }

        Set<String> missingSkills = new HashSet<>(requiredSkills);
        missingSkills.removeAll(userSkills);

        Set<String> missingCerts = new HashSet<>(requiredCerts);
        missingCerts.removeAll(userCerts);

        List<String> gapItems = new ArrayList<>();
        gapItems.addAll(missingSkills);
        gapItems.addAll(missingCerts);

        List<CareerStep> steps = buildCareerSteps(missingSkills);
        int totalMonths = steps.stream().mapToInt(CareerStep::estimatedMonths).sum();
        List<String> requiredCertList = new ArrayList<>(requiredCerts);

        CareerPath careerPath = new CareerPath(steps, totalMonths, requiredCertList);

        return new SimulationResult(currentPosition, targetJobCategory, careerPath, gapItems);
    }

    private List<CareerStep> buildCareerSteps(Set<String> missingSkills) {
        List<CareerStep> steps = new ArrayList<>();
        if (missingSkills.isEmpty()) {
            steps.add(new CareerStep(
                    "포트폴리오 강화",
                    "현재 보유한 기술로 포트폴리오를 강화하세요.",
                    2,
                    List.of()
            ));
            return steps;
        }

        List<String> skillList = new ArrayList<>(missingSkills);
        int chunkSize = Math.max(1, skillList.size() / 3 + 1);
        List<List<String>> chunks = new ArrayList<>();
        for (int i = 0; i < skillList.size(); i += chunkSize) {
            chunks.add(skillList.subList(i, Math.min(i + chunkSize, skillList.size())));
        }

        String[] stepNames = {"기초 역량 강화", "핵심 기술 습득", "심화 학습 및 프로젝트"};
        for (int i = 0; i < chunks.size(); i++) {
            String name = i < stepNames.length ? stepNames[i] : "추가 학습 " + (i + 1);
            steps.add(new CareerStep(
                    name,
                    String.join(", ", chunks.get(i)) + " 학습",
                    3,
                    chunks.get(i)
            ));
        }
        return steps;
    }
}
