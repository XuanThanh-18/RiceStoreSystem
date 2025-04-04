package com.toby.ricemanagersystem.config;

import com.toby.ricemanagersystem.security.CustomUserDetailsService;
import com.toby.ricemanagersystem.security.RestAuthenticationEntryPoint;
import com.toby.ricemanagersystem.security.TokenAuthenticationFilter;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.List;

@Configuration
@RequiredArgsConstructor
@EnableMethodSecurity(
        securedEnabled = true,
        jsr250Enabled = true,
        prePostEnabled = true
)
public class SecurityConfig {

    private final CustomUserDetailsService customUserDetailsService;
    private final RestAuthenticationEntryPoint unauthorizedHandler;
    private final TokenAuthenticationFilter tokenAuthenticationFilter;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .cors(cors -> cors.configurationSource(corsConfigurationSource())) // cho phép truy cập từ frontend ở domain khác
                .csrf(AbstractHttpConfigurer::disable)
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS)) // không lưu thông tin đăng nhập trong session – dùng JWT (token) để xác thực thay vì session truyền thống
                .exceptionHandling(exception -> exception.authenticationEntryPoint(unauthorizedHandler)) // Khi người dùng không có quyền truy cập, sẽ chạy custom entry point unauthorizedHandler để trả về JSON thay vì chuyển hướng đến trang login
                .authorizeHttpRequests(auth -> auth  //Cho phép các đường dẫn tĩnh
                        .requestMatchers("/",
                                "/error",
                                "/favicon.ico",
                                "/*.png",
                                "/*.gif",
                                "/*.svg",
                                "/*.jpg",
                                "/*.html",
                                "/*.css",
                                "/*.js")
                        .permitAll()
                        .requestMatchers("/api/auth/forgot-password").permitAll()
                        .requestMatchers("/api/auth/**", "/ws/**", "/api/url-sharing/**") //Cho phép các API liên quan tới xác thực, WebSocket, chia sẻ URL được truy cập tự do.
                        .permitAll()
                        .anyRequest().authenticated() // Mọi request còn lại bắt buộc phải đăng nhập mới truy cập được.
                )
                //Chèn custom JWT filter (tokenAuthenticationFilter) vào trước filter mặc định (UsernamePasswordAuthenticationFilter)
                // để xử lý token trong các request.
                .addFilterBefore(tokenAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }
    //để xử lý việc xác thực (login).
    @Bean
    public AuthenticationManager authenticationManager() {
        return authenticationProvider()::authenticate;
    }

    @Bean
    public AuthenticationProvider authenticationProvider() {
        // xác thực user từ database
        DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider();
        //load thông tin người dùng
        authProvider.setUserDetailsService(customUserDetailsService);
        //Mã hóa mật khẩu
        authProvider.setPasswordEncoder(passwordEncoder());
        return authProvider;
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    // ✅ CorsConfigurationSource thay vì CorsFilter
    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration config = new CorsConfiguration();
        config.setAllowCredentials(true); //Cho phép gửi cookie/token từ frontend
        config.setAllowedOrigins(List.of("http://localhost:3000")); //Cho phép domain frontend gọi API này
        config.setAllowedHeaders(List.of("Authorization", "Cache-Control", "Content-Type")); //Cho phép các header này được gửi lên.
        config.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "OPTIONS")); //Cho phép các method HTTP cụ thể.

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", config); //Gán cấu hình CORS cho toàn bộ các endpoint
        return source;
    }
}
