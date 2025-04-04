package com.toby.ricemanagersystem.service.impl;

import com.toby.ricemanagersystem.dto.request.UpdateProfileRequest;
import com.toby.ricemanagersystem.dto.response.UserProfileResponse;
import com.toby.ricemanagersystem.model.User;
import com.toby.ricemanagersystem.model.UserProfile;
import com.toby.ricemanagersystem.repository.UserProfileRepository;
import com.toby.ricemanagersystem.repository.UserRepository;
import com.toby.ricemanagersystem.service.UserService;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserProfileServiceImpl implements com.toby.ricemanagersystem.service.UserProfileService {
    private final UserProfileRepository userProfileRepository;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final UserService userService;
    private final ModelMapper modelMapper = new ModelMapper();

    @Override
    public boolean isEmailExist(String email) {return userProfileRepository.existsByEmail(email);}

    @Override
    public UserProfileResponse getCurrentUserProfile() {
        User currentUser = userService.getCurrentUser();
        UserProfile userProfile = userProfileRepository.findByUser(currentUser);
        return modelMapper.map(userProfile, UserProfileResponse.class);
    }

    @Override
    public UserProfileResponse updateCurrentUserProfile(UpdateProfileRequest request) {
        User currentUser = userService.getCurrentUser();
        UserProfile currentUserProfile = userProfileRepository.findByUser(currentUser);
        if(currentUserProfile == null) {
            UserProfile newUserProfile = UserProfile.builder()
                    .email(request.getEmail())
                    .fullName(request.getFullName())
                    .gender(request.getGender())
                    .dateOfBirth(request.getDateOfBirth())
                    .phoneNumber(request.getPhoneNumber())
                    .identityNumber(request.getIdentityNumber())
                    .user(currentUser)
                    .build();
            return modelMapper.map(userProfileRepository.save(newUserProfile), UserProfileResponse.class);
        }else{
            currentUserProfile = UserProfile.builder()
                    .email(request.getEmail())
                    .fullName(request.getFullName())
                    .gender(request.getGender())
                    .dateOfBirth(request.getDateOfBirth())
                    .phoneNumber(request.getPhoneNumber())
                    .identityNumber(request.getIdentityNumber())
                    .build();
            return modelMapper.map(userProfileRepository.save(currentUserProfile), UserProfileResponse.class);
        }
    }

    @Override
    public void changePassword(String newPassword) {
        User currentUser = userService.getCurrentUser();
        if(newPassword.equals(currentUser.getPassword())) {
            throw new RuntimeException("Passwords is not changed");
        }
        if(newPassword.isEmpty()) {
            throw new RuntimeException("Password is empty");
        }
        currentUser.setPassword(passwordEncoder.encode(newPassword));
        User updatedUser = userRepository.save(currentUser);
        System.out.println("Password changed to " + updatedUser.getPassword());
    }

    @Override
    public UserProfileResponse getUserProfileByUser(Long userId) {
        if(userId == null) {
            throw new RuntimeException("User id is null");
        }
        UserProfile userProfile = userProfileRepository.findByUserId(userId);
        return modelMapper.map(userProfile, UserProfileResponse.class);
    }
}
