package com.oda.application.user;

import com.oda.support.error.CoreException;
import com.oda.support.error.ErrorType;
import com.oda.domain.user.User;
import com.oda.domain.user.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class UserService {

    private final UserRepository userRepository;

    @Transactional(readOnly = true)
    public User getUser(Long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new CoreException(ErrorType.NOT_FOUND, "User not found with id: " + userId));
    }

    public void updateConsent(Long userId, boolean consentPersonalInfo, boolean consentSensitiveInfo) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new CoreException(ErrorType.NOT_FOUND, "User not found with id: " + userId));
        user.updateConsent(consentPersonalInfo, consentSensitiveInfo);
        userRepository.save(user);
    }

    public void updateName(Long userId, String name) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new CoreException(ErrorType.NOT_FOUND, "User not found with id: " + userId));
        user.updateName(name);
        userRepository.save(user);
    }

    public void deleteUser(Long userId) {
        userRepository.findById(userId)
                .orElseThrow(() -> new CoreException(ErrorType.NOT_FOUND, "User not found with id: " + userId));
        // Mark as deleted or remove - delegate to repository
        userRepository.deleteById(userId);
    }
}
