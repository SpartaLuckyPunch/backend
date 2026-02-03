package com.example.burnchuck.common.utils;

import com.example.burnchuck.common.enums.UserRole;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.JwtParser;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import jakarta.annotation.PostConstruct;
import java.time.Duration;
import java.time.Instant;
import java.util.Date;
import javax.crypto.SecretKey;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.util.ObjectUtils;

@Component
public class JwtUtil {

    public static final String BEARER_PREFIX = "Bearer ";
    private static final long ACCESS_TOKEN_TIME = 60 * 60 * 1000L; // 1시간
    private static final long REFRESH_TOKEN_TIME = 7 * 24 * 60 * 60 * 1000L; // 1주
    public static final String TOKEN_TYPE_ACCESS = "access";
    public static final String TOKEN_TYPE_REFRESH = "refresh";

    @Value("${jwt.secret.key}")
    private String secretKeyString;

    private SecretKey key;
    private JwtParser parser;

    @PostConstruct
    public void init() {
        byte[] bytes = Decoders.BASE64.decode(secretKeyString);
        this.key = Keys.hmacShaKeyFor(bytes);
        this.parser = Jwts.parser()
            .verifyWith(this.key)
            .build();
    }

    // 토큰 생성
    public String generateAccessToken(Long id, String email, String nickname, UserRole userRole) {
        Date now = new Date();

        return BEARER_PREFIX + Jwts.builder()
            .claim("type", TOKEN_TYPE_ACCESS)
            .claim("id", id)
            .claim("email", email)
            .claim("nickname", nickname)
            .claim("role", userRole.name())
            .issuedAt(now)
            .expiration(new Date(now.getTime() + ACCESS_TOKEN_TIME))
            .signWith(key, Jwts.SIG.HS256)
            .compact();
    }

    // Refresh 토큰 생성 (1주 뒤 만료)
    public String generateRefreshToken(Long userId) {
        Date now = new Date();

        return Jwts.builder()
            .issuedAt(now)
            .claim("type", TOKEN_TYPE_REFRESH)
            .claim("id", userId)
            .expiration(new Date(now.getTime() + REFRESH_TOKEN_TIME))
            .signWith(key, Jwts.SIG.HS256)
            .compact();
    }

    // 토큰 검증
    public boolean validateToken(String token) {

        if (token == null || token.isBlank()) {
            return false;
        }

        try {
            parser.parseSignedClaims(token);
            return true;
        } catch (JwtException | IllegalArgumentException e) {
            return false;
        }
    }

    public boolean isExpired(String token){

        try {
            Date expDate = extractAllClaims(token).getExpiration();
            return new Date().after(expDate);
        } catch (ExpiredJwtException e){
            return true;
        }
    }

    public boolean expireInTwoDays(String token) {

        Instant now = Instant.now();

        long expireDate = extractAllClaims(token).getExpiration().getTime();
        Instant expireAt = Instant.ofEpochMilli(expireDate);

        long remainDays = Duration.between(now, expireAt).toDays();

        if (remainDays < 2) {
            return true;
        }

        return false;
    }

    public boolean isRefreshToken(String token) {

        String type = extractAllClaims(token).get("type", String.class);
        return ObjectUtils.nullSafeEquals(type, TOKEN_TYPE_REFRESH);
    }

    // 토큰 복호화
    private Claims extractAllClaims(String token) {
        return parser.parseSignedClaims(token).getPayload();
    }

    public Long extractId(String token) {
        return extractAllClaims(token).get("id", Long.class);
    }

    public String extractEmail(String token) {
        return extractAllClaims(token).get("email", String.class);
    }

    public String extractNickname(String token) {
        return extractAllClaims(token).get("nickname", String.class);
    }

    public String extractRole(String token) {
        return extractAllClaims(token).get("role", String.class);
    }
}
