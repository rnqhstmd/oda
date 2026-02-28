package com.oda.application.user;

import com.oda.application.user.dto.ProfileResult;
import com.oda.application.user.dto.UpdateProfileCommand;
import com.oda.support.error.CoreException;
import com.oda.support.error.ErrorType;
import com.oda.domain.user.UserProfile;
import com.oda.domain.user.UserProfileRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class ProfileService {

    private final UserProfileRepository userProfileRepository;

    @Transactional(readOnly = true)
    public ProfileResult getProfile(Long userId) {
        UserProfile profile = userProfileRepository.findByUserId(userId)
                .orElseThrow(() -> new CoreException(ErrorType.NOT_FOUND, "User not found with id: " + userId));
        return ProfileResult.from(profile);
    }

    public ProfileResult updateProfile(Long userId, UpdateProfileCommand command) {
        UserProfile profile = userProfileRepository.findByUserId(userId)
                .orElseGet(() -> UserProfile.create(userId));

        profile.update(
                command.birthDate(),
                command.sido(),
                command.sigungu(),
                command.employmentStatus(),
                command.educations(),
                command.workExperiences(),
                command.certifications(),
                command.skills(),
                command.targetJobCategories()
        );

        UserProfile saved = userProfileRepository.save(profile);
        return ProfileResult.from(saved);
    }
}
