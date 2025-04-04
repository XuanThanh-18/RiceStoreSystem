package com.toby.ricemanagersystem.service.impl;

import com.toby.ricemanagersystem.exception.CustomServiceException;
import com.toby.ricemanagersystem.model.ForgotPasswordToken;
import com.toby.ricemanagersystem.model.User;
import com.toby.ricemanagersystem.model.UserProfile;
import com.toby.ricemanagersystem.repository.ForgotPasswordTokenRepository;
import com.toby.ricemanagersystem.repository.UserProfileRepository;
import com.toby.ricemanagersystem.repository.UserRepository;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.dao.DataAccessException;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.io.UnsupportedEncodingException;
import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ForgotPasswordServiceImpl implements com.toby.ricemanagersystem.service.ForgotPasswordService {

    @Value("${mss.app.fe-url}")
    private String feHost;

    @Value("${spring.mail.username}")
    private String fromMail;

    private static final int MINUTES = 10;

    private final ForgotPasswordTokenRepository forgotPasswordTokenRepository;
    private final UserProfileRepository userProfileRepository;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JavaMailSender javaMailSender;

    @Override
    public String generateToken() {
        return UUID.randomUUID().toString();
    }

    @Override
    public LocalDateTime expireTimeRange() {
        return LocalDateTime.now().plusMinutes(MINUTES);
    }

    @Override
    public void sendEmail(String to, String subject, String emailLink) throws MessagingException, UnsupportedEncodingException {
        MimeMessage message = javaMailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message, true);

        String emailContent = "<p>Xin chào!</p>"
                + "Truy cập đường link bên dưới để đặt lại mật khẩu"
                + "<p><a href=\"" + emailLink + "\">Thay đổi mật khẩu</a></p>"
                + "<br>"
                + "Bỏ qua nếu bạn không thực hiện yêu cầu này!";

        helper.setText(emailContent, true);
        helper.setFrom(fromMail, "Toby Xuan Thanh");
        helper.setSubject(subject);
        helper.setTo(to);

        javaMailSender.send(message);
    }

    @Override
    public String createForgotPasswordToken(String email) {
        try {
            UserProfile userProfile = userProfileRepository.findByEmail(email)
                    .orElseThrow(() -> new CustomServiceException("Email không tồn tại trong hệ thống"));

            User user = userProfile.getUser();
            ForgotPasswordToken forgotPasswordToken = new ForgotPasswordToken();
            forgotPasswordToken.setExpireTime(expireTimeRange());
            forgotPasswordToken.setToken(generateToken());
            forgotPasswordToken.setUser(user);
            forgotPasswordToken.setUsed(false);

            forgotPasswordTokenRepository.save(forgotPasswordToken);

            return feHost + "reset-password?token=" + forgotPasswordToken.getToken();
        } catch (DataAccessException e) {
            throw new CustomServiceException("Lỗi khi tạo token quên mật khẩu: " + e.getMessage(), e);
        }
    }

    @Override
    public void requestForgotPassword(String email) {
        String emailLink = createForgotPasswordToken(email);
        try {
            sendEmail(email, "Đặt lại mật khẩu", emailLink);
        } catch (MessagingException | UnsupportedEncodingException e) {
            throw new CustomServiceException("Lỗi khi gửi email: " + e.getMessage(), e);
        }
    }

    @Override
    public boolean isExpired(String forgotPasswordToken) {
        ForgotPasswordToken token = forgotPasswordTokenRepository.findByToken(forgotPasswordToken)
                .orElseThrow(() -> new CustomServiceException("Token không tồn tại"));

        return LocalDateTime.now().isAfter(token.getExpireTime());
    }

    @Override
    public boolean checkIsUsed(String forgotPasswordToken) {
        ForgotPasswordToken token = forgotPasswordTokenRepository.findByToken(forgotPasswordToken)
                .orElseThrow(() -> new CustomServiceException("Token không tồn tại"));

        return token.isUsed();
    }

    @Override
    public void resetPassword(String token, String newPassword) {
        try {
            ForgotPasswordToken forgotToken = forgotPasswordTokenRepository.findByToken(token)
                    .orElseThrow(() -> new CustomServiceException("Token quên mật khẩu không hợp lệ"));

            if (checkIsUsed(token)) {
                throw new CustomServiceException("Token quên mật khẩu đã được sử dụng");
            } else if (isExpired(token)) {
                throw new CustomServiceException("Token quên mật khẩu đã hết hạn");
            }

            if (newPassword == null || newPassword.isEmpty()) {
                throw new CustomServiceException("Mật khẩu mới không thể để trống");
            }

            User user = forgotToken.getUser();
            user.setPassword(passwordEncoder.encode(newPassword));
            userRepository.save(user);

            forgotToken.setUsed(true);
            forgotPasswordTokenRepository.save(forgotToken);

        } catch (DataAccessException e) {
            throw new CustomServiceException("Lỗi khi cập nhật mật khẩu người dùng: " + e.getMessage(), e);
        }
    }
}
