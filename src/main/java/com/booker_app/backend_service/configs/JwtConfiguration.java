package com.booker_app.backend_service.configs;

import com.booker_app.backend_service.models.User;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import java.util.Date;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.util.StringUtils;

@Slf4j
@Configuration
public class JwtConfiguration {

    @Value("${JWT_SECRET}")
    private String jwtSecret;

    public JwtConfiguration() {
        // sonar
    }

    public String generateToken(User user) {
        return Jwts.builder()
                .setSubject(user.getEmail())
                .claim("userId", user.getId())
                .claim("fullName", user.getFullName())
                .claim("role", user.getUserRole())
                .claim("email", user.getEmail())
                .setIssuedAt(new Date())
                .setExpiration(
                        new Date(System.currentTimeMillis() + 1000 * 60 * 60)) // 1 hour
                .signWith(SignatureAlgorithm.HS256, jwtSecret.getBytes())
                .compact();
    }

    public Claims parseToken(String token) {
        return Jwts.parser().setSigningKey(jwtSecret.getBytes()).parseClaimsJws(token).getBody();
    }

    public boolean isTokenExpired(String token) {
        if (!StringUtils.hasLength(token)) {
            return true;
        }

        Claims tokenClaims = parseToken(token);
        return tokenClaims.getExpiration().before(new Date());
    }
}
