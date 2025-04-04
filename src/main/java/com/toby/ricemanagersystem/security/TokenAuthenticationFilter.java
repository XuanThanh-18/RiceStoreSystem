package com.toby.ricemanagersystem.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
@RequiredArgsConstructor
public class TokenAuthenticationFilter extends OncePerRequestFilter{ //thuc thi 1 lan sau moi lan request

    private final TokenProvider tokenProvider;
    private final CustomUserDetailsService userDetailsService;
    private static final Logger logger = LoggerFactory.getLogger(TokenAuthenticationFilter.class);

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        try {
            // Lấy JWT từ request
            String jwt = getJwtFromRequest(request);
            // ✅ Bỏ qua xác thực với các endpoint public
            String path = request.getServletPath();
            if (path.startsWith("/api/auth/")) {
                filterChain.doFilter(request, response);
                return;
            }

            // Kiểm tra nếu token có tồn tại và hợp lệ
            if (StringUtils.hasText(jwt) && tokenProvider.validateToken(jwt)) {

                // Lấy userId từ token
                Long userId = tokenProvider.getUserIdFromToken(jwt);

                // Lấy thông tin người dùng từ cơ sở dữ liệu
                UserDetails userDetails = userDetailsService.loadUserById(userId);

                // Tạo đối tượng xác thực (authentication) cho người dùng
                UsernamePasswordAuthenticationToken authentication =
                        new UsernamePasswordAuthenticationToken(
                                userDetails,
                                null, //    Không có mật khẩu
                                userDetails.getAuthorities()); // Quyền của người dùng

                // Cung cấp chi tiết xác thực cho đối tượng authentication
                authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                // Đặt đối tượng authentication vào SecurityContext
                SecurityContextHolder.getContext().setAuthentication(authentication);
            }
        } catch (Exception e) {
            logger.error("Cannot set user authentication: {}", e);
        }
        filterChain.doFilter(request, response);
    }


    // Trích xuất token từ header của request:
    private String getJwtFromRequest(HttpServletRequest request) {
        String bearerToken = request.getHeader("Authorization");
        //header Authorization có chứa chuỗi bắt đầu bằng Bearer , thì token sẽ được tách ra và trả về.
        if (StringUtils.hasText(bearerToken) && bearerToken.startsWith("Bearer ")) {
            return bearerToken.substring(7);
        }
        return null;
    }
}
