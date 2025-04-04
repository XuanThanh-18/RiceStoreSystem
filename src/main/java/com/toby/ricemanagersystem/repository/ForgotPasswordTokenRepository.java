package com.toby.ricemanagersystem.repository;

import com.toby.ricemanagersystem.model.ForgotPasswordToken;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ForgotPasswordTokenRepository extends JpaRepository<ForgotPasswordToken,Long> {
    Optional<ForgotPasswordToken> findByToken(String token);
}
