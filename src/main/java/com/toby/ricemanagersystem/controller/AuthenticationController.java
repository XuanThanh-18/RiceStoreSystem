package com.toby.ricemanagersystem.controller;

import com.toby.ricemanagersystem.dto.RegisterRequestDTO;
import com.toby.ricemanagersystem.payload.request.LoginRequest;
import com.toby.ricemanagersystem.payload.response.AuthResponse;
import com.toby.ricemanagersystem.payload.response.MessageResponse;
import com.toby.ricemanagersystem.repository.UserProfileRepository;
import com.toby.ricemanagersystem.repository.UserRepository;
import com.toby.ricemanagersystem.security.TokenProvider;
import com.toby.ricemanagersystem.security.UserPrincipal;
import com.toby.ricemanagersystem.service.ForgotPasswordService;
import com.toby.ricemanagersystem.service.UserProfileService;
import com.toby.ricemanagersystem.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthenticationController {
    private final AuthenticationManager authenticationManager;
    private final TokenProvider tokenProvider;
    private final UserService userService;
    private final UserProfileService userProfileService;
    private final ForgotPasswordService forgotPasswordService;


    @PostMapping("/login")
    public ResponseEntity<?> authenticateUser(@RequestBody LoginRequest loginRequest) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(loginRequest.getUsername(), loginRequest.getPassword())
        );
        SecurityContextHolder.getContext().setAuthentication(authentication);
        String token = tokenProvider.createToken(authentication);

        UserPrincipal userPrincipal = (UserPrincipal) authentication.getPrincipal();
        String role = userService.getRoleByUsername(loginRequest.getUsername());
        String status = userService.getStatusByUsername(loginRequest.getUsername());

        return ResponseEntity.ok(new AuthResponse(token,
                userPrincipal.getId(),userPrincipal.getUsername(),
                role, status));
    }

    @PostMapping("/register")
    public ResponseEntity<?> registerUser(@RequestBody RegisterRequestDTO registerRequestDTO) {
        userService.registerUser(registerRequestDTO);
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        registerRequestDTO.getUsername(),
                        registerRequestDTO.getPassword()
                )
        );
        SecurityContextHolder.getContext().setAuthentication(authentication);
        return ResponseEntity.ok(new MessageResponse("User registered successfully!"));
    }
    @PostMapping("/forgot-password")
    public ResponseEntity<?> forgotPassword(@RequestParam("email") String email) {
        try {
            forgotPasswordService.requestForgotPassword(email);
            return ResponseEntity.ok("hihi");
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage()); // Consider a more informative error structure
        } catch (Exception e) {
            return ResponseEntity.internalServerError().build(); // General error handling, consider logging or a more specific error message
        }
    }

    @PostMapping("/reset-password")
    public ResponseEntity<?> resetPassword(@RequestParam("token") String token, @RequestParam("newPassword") String newPassword) {
        try {
            if (forgotPasswordService.checkIsUsed(token)) {
                return ResponseEntity.badRequest().body("Token của bạn đã được dùng!");
            } else if (forgotPasswordService.isExpired(token)) {
                return ResponseEntity.badRequest().body("Token của bạn đã hết hạn! Vui lòng thử lại.");
            } else {
                forgotPasswordService.resetPassword(token, newPassword);
                return ResponseEntity.ok("Đặt lại mật khẩu thành công! Hãy đăng nhập lại.");
            }
        } catch (Exception e) {
            return ResponseEntity.internalServerError().build(); // General error handling, consider logging or a more specific error message
        }
    }

    @GetMapping("/check-token-valid")
    public ResponseEntity<?> checkTokenValid(@RequestParam("token") String token) {
        boolean isExpired = forgotPasswordService.isExpired(token);
        boolean isUsed = forgotPasswordService.checkIsUsed(token);

        if (isExpired) {
            return ResponseEntity.badRequest().body("Token đã hết hạn");
        } else if (isUsed) {
            return ResponseEntity.badRequest().body("Token đã được sử dụng");
        } else {
            return ResponseEntity.ok().body("Token hợp lệ");
        }
    }

    @GetMapping("/check-username")
    public ResponseEntity<?> checkUsername(@RequestParam("username") String username) {
        boolean exists = userService.checkUsernameExists(username);
        return ResponseEntity.ok(exists);
    }

    @GetMapping("/check-email")
    public ResponseEntity<?> checkEmail(@RequestParam("email") String email) {
        boolean exists = userProfileService.isEmailExist(email);
        return ResponseEntity.ok(exists);
    }
}
