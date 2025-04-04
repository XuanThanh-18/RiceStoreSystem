package com.toby.ricemanagersystem.security;

import io.jsonwebtoken.*;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.util.Date;

@Component
public class TokenProvider {
    private static final Logger logger = LoggerFactory.getLogger(TokenProvider.class);

    // mã hóa và giải mã JWT
    @Value("${mss.app.tokenSecret}")
    private String jwtSecret;

    //thời gian hết hạn của token
    @Value("${mss.app.tokenExpirationMsec}")
    private int jwtExpirationMs;

    public String createToken(Authentication authentication) {
        //Thông tin người dùng được lưu
        UserPrincipal userPrincipal = (UserPrincipal) authentication.getPrincipal();
        String token = Jwts.builder()
                .setSubject(String.valueOf(userPrincipal.getId())) // co the lay Id tu token
                .setIssuedAt(new Date()) // set time bat dau token
                .setExpiration(new Date((new Date()).getTime() + jwtExpirationMs)) // hạn token
                .signWith(key(), SignatureAlgorithm.HS256) // thuat toan ma hoa
                .compact(); // tao tra ve duoi dang chuoi
        System.out.println(token);
        return token;
    }

    private Key key() {
        return Keys.hmacShaKeyFor(Decoders.BASE64.decode(jwtSecret));
    }

    public long getUserIdFromToken(String token) {
        return Long.parseLong(Jwts.parserBuilder().setSigningKey(key()).build()
                .parseClaimsJws(token).getBody().getSubject());
    }

    public boolean validateToken(String authToken) {
        try {
            Jwts.parserBuilder().setSigningKey(key()).build().parse(authToken);
            return true;
        } catch (MalformedJwtException e) {
            logger.error("Invalid JWT token: {}", e.getMessage());
        } catch (ExpiredJwtException e) {
            logger.error("JWT token is expired: {}", e.getMessage());
        } catch (UnsupportedJwtException e) {
            logger.error("JWT token is unsupported: {}", e.getMessage());
        } catch (IllegalArgumentException e) {
            logger.error("JWT claims string is empty: {}", e.getMessage());
        }
        return false;
    }
}
