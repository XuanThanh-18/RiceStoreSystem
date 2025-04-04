package com.toby.ricemanagersystem.service;

import com.toby.ricemanagersystem.dto.request.UpdateProfileRequest;
import com.toby.ricemanagersystem.dto.response.UserProfileResponse;

public interface UserProfileService {
    boolean isEmailExist(String email);

    UserProfileResponse getCurrentUserProfile();

    UserProfileResponse updateCurrentUserProfile(UpdateProfileRequest request);

    void changePassword(String newPassword);

    UserProfileResponse getUserProfileByUser(Long userId);
}
