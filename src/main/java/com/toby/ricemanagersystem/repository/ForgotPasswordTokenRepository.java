package com.toby.ricemanagersystem.repository;

import com.toby.ricemanagersystem.model.ForgotPasswordToken;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ForgotPasswordTokenRepository extends JpaRepository<ForgotPasswordToken,Long> {
    ForgotPasswordToken findByToken(String token);
}
