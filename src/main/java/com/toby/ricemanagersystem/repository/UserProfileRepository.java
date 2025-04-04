package com.toby.ricemanagersystem.repository;

import com.toby.ricemanagersystem.model.User;
import com.toby.ricemanagersystem.model.UserProfile;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserProfileRepository extends JpaRepository<UserProfile, Long> {
    UserProfile findByUser(User user);
    UserProfile findByEmail(String email);
    boolean existsByEmail(String email);
    UserProfile findByUserId(Long userId);
}
