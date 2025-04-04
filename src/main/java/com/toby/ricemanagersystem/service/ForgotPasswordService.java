package com.toby.ricemanagersystem.service;

import jakarta.mail.MessagingException;

import java.io.UnsupportedEncodingException;
import java.time.LocalDateTime;

public interface ForgotPasswordService {
    String generateToken();

    LocalDateTime expireTimeRange();

    void sendEmail(String to, String subject, String emailLink) throws MessagingException, UnsupportedEncodingException;

    String createForgotPasswordToken(String email);

    void requestForgotPassword(String email);

    boolean isExpired(String forgotPasswordToken);

    boolean checkIsUsed(String forgotPasswordToken);

    void resetPassword(String token, String newPassword);
}
