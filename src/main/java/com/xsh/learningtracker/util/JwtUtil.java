package com.xsh.learningtracker.util;

import java.util.Date;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;

@Component
public class JwtUtil {

    @Value("${jwt.secret:defaultSecretKeydefaultSecretKeydefaultSecretKey}")
    private String secret;

    @Value("${jwt.expiration:86400000}")
    private long expiration;

    public String generateToken(String username) {
        Date now = new Date();
        return Jwts.builder()
                .claims()
                .subject(username)
                .issuedAt(now)
                .expiration(new Date(now.getTime() + expiration))
                .and()
                .signWith(Keys.hmacShaKeyFor(secret.getBytes()))
                .compact();
    }

    // 添加刷新令牌的方法
    public String refreshToken(String token) {
        // 从旧令牌中提取用户名
        String username = getUsernameFromToken(token);
        // 生成新令牌
        return generateToken(username);
    }

    // 获取令牌过期时间
    public Date getExpirationDateFromToken(String token) {
        Claims claims = Jwts.parser()
                .verifyWith(Keys.hmacShaKeyFor(secret.getBytes()))
                .build()
                .parseSignedClaims(token)
                .getPayload();

        return claims.getExpiration();
    }

    // 检查令牌是否即将过期（比如还有5分钟就过期）
    public boolean isTokenAboutToExpire(String token) {
        try {
            Date expiration = getExpirationDateFromToken(token);
            return expiration.getTime() - System.currentTimeMillis() < 300000; // 小于5分钟
        } catch (Exception e) {
            return false;
        }
    }

    public String getUsernameFromToken(String token) {
        Claims claims = Jwts.parser()
                .verifyWith(Keys.hmacShaKeyFor(secret.getBytes()))
                .build()
                .parseSignedClaims(token)
                .getPayload();

        return claims.getSubject();
    }

    public boolean validateToken(String token) {
        try {
            Jwts.parser()
                    .verifyWith(Keys.hmacShaKeyFor(secret.getBytes()))
                    .build()
                    .parseSignedClaims(token);
            return true;
        } catch (Exception e) {
            return false;
        }
    }
}
