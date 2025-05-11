package com.xsh.learningtracker.util;

import java.util.Date;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.xsh.learningtracker.exception.TokenRefreshException;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.UnsupportedJwtException;
import io.jsonwebtoken.security.Keys;
import io.jsonwebtoken.security.SignatureException;

@Component
public class JwtUtil {

    private static final Logger logger = LoggerFactory.getLogger(JwtUtil.class);

    @Value("${jwt.secret:defaultSecretKeydefaultSecretKeydefaultSecretKey}")
    private String secret;

    @Value("${jwt.expiration:86400000}")
    private long expiration;

    @Value("${jwt.refresh-expiration:604800000}")
    private long refreshExpiration = 604800000; // 默认7天

    public String generateToken(String username, Integer userId) { // Added userId parameter
        Date now = new Date();
        return Jwts.builder()
                .claims()
                .subject(username)
                .add("userId", userId) // Added userId claim
                .issuedAt(now)
                .expiration(new Date(now.getTime() + expiration))
                .and()
                .signWith(Keys.hmacShaKeyFor(secret.getBytes()))
                .compact();
    }

    // 添加一个专门用于生成长期有效刷新令牌的方法
    // Refresh token might also benefit from userId if it's used to re-issue access
    // tokens with userId
    public String generateRefreshToken(String username, Integer userId) { // Added userId parameter
        Date now = new Date();
        return Jwts.builder()
                .claims()
                .subject(username)
                .issuedAt(now)
                .expiration(new Date(now.getTime() + refreshExpiration))
                .and()
                .claim("isRefreshToken", true) // 标记为刷新令牌
                .signWith(Keys.hmacShaKeyFor(secret.getBytes()))
                .compact();
    }

    // 完善刷新令牌的方法
    public String refreshToken(String token) {
        try {
            // 针对过期令牌的特殊处理：使用validateTokenIgnoreExpiration而不是validateToken
            // 这样可以允许刷新已过期但签名有效的令牌
            if (!validateTokenIgnoreExpiration(token)) {
                logger.error("无法刷新令牌：令牌签名无效");
                throw new TokenRefreshException(token.substring(0, Math.min(10, token.length())) + "...", "令牌签名无效");
            }

            // 从旧令牌中提取用户名和用户ID（即使令牌已过期）
            String username = getUsernameFromToken(token);
            Integer userId = extractUserId(token); // Attempt to extract userId as well
            logger.info("成功从令牌中提取用户名: {}, 用户ID: {}, 准备刷新", username, userId);

            // 检查令牌是否是刷新令牌（如果实现了刷新令牌机制）
            Claims claims = extractAllClaims(token);
            Boolean isRefreshToken = claims.get("isRefreshToken", Boolean.class);

            // 如果是刷新令牌，则生成访问令牌，否则直接刷新当前令牌
            if (isRefreshToken != null && isRefreshToken) {
                logger.debug("使用刷新令牌生成新的访问令牌");
                if (userId == null) { // Fallback if userId couldn't be extracted from refresh token directly
                    // This would require fetching userId from username via DB, or assuming refresh
                    // token always has it
                    // For simplicity, let's assume refresh token should also contain userId or this
                    // path needs more logic
                    logger.warn(
                            "UserId not found in refresh token claims, cannot generate access token with userId directly from refresh token.");
                    // Potentially throw an error or generate token without userId if allowed by
                    // business logic
                    // throw new TokenRefreshException(token, "Cannot re-issue access token without
                    // userId from refresh token.");
                    // Or, if username is enough for a new session:
                    // return generateToken(username, null); // Or fetch userId based on username
                }
                return generateToken(username, userId); // Generate access token with userId
            } else {
                logger.debug("刷新现有访问令牌");
                // 获取剩余有效期，如果剩余有效期太短，生成全新令牌
                long remainingTime = claims.getExpiration().getTime() - System.currentTimeMillis();
                if (remainingTime < 600000) { // 少于10分钟
                    return generateToken(username, userId);
                }
                // 生成新令牌
                return generateToken(username, userId);
            }
        } catch (ExpiredJwtException e) {
            // 对于过期令牌，我们仍然可以尝试刷新
            logger.info("检测到令牌已过期，尝试从过期令牌中提取用户信息并生成新令牌");
            String username = e.getClaims().getSubject();
            Integer userId = e.getClaims().get("userId", Integer.class); // Extract userId from expired token
            if (username != null && userId != null) {
                logger.info("成功从过期令牌中提取用户名: {}, 用户ID: {}, 正在生成新令牌", username, userId);
                return generateToken(username, userId);
            } else {
                logger.error("无法从过期令牌中提取完整的用户信息 (username or userId is null)");
                throw new TokenRefreshException(token.substring(0, Math.min(10, token.length())) + "...",
                        "令牌已过期且无法提取用户信息");
            }
        } catch (Exception e) {
            logger.error("刷新令牌时发生错误: {}", e.getMessage());
            throw new TokenRefreshException(token.substring(0, Math.min(10, token.length())) + "...",
                    "令牌刷新失败: " + e.getMessage());
        }
    }

    // 提取所有声明
    private Claims extractAllClaims(String token) {
        return Jwts.parser()
                .verifyWith(Keys.hmacShaKeyFor(secret.getBytes()))
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }

    // 获取令牌过期时间
    public Date getExpirationDateFromToken(String token) {
        try {
            Claims claims = extractAllClaims(token);
            return claims.getExpiration();
        } catch (Exception e) {
            logger.error("从令牌中获取过期时间失败", e);
            return null;
        }
    }

    // 检查令牌是否即将过期（比如还有5分钟就过期）
    public boolean isTokenAboutToExpire(String token) {
        try {
            Date expiration = getExpirationDateFromToken(token);
            if (expiration == null)
                return true;

            return expiration.getTime() - System.currentTimeMillis() < 300000; // 小于5分钟
        } catch (Exception e) {
            logger.error("检查令牌是否即将过期时发生错误", e);
            return true;
        }
    }

    public String getUsernameFromToken(String token) {
        try {
            Claims claims = extractAllClaims(token);
            return claims.getSubject();
        } catch (ExpiredJwtException e) {
            // 即使令牌过期，我们仍然需要获取用户名（用于刷新令牌）
            return e.getClaims().getSubject();
        } catch (Exception e) {
            logger.error("从令牌中提取用户名失败", e);
            return null;
        }
    }

    public Integer extractUserId(String token) {
        try {
            Claims claims = extractAllClaims(token);
            return claims.get("userId", Integer.class);
        } catch (ExpiredJwtException e) {
            // Attempt to get userId even if token is expired, for refresh purposes
            return e.getClaims().get("userId", Integer.class);
        } catch (Exception e) {
            logger.error("从令牌中提取用户ID失败", e);
            return null;
        }
    }

    public boolean validateToken(String token) {
        try {
            Jwts.parser()
                    .verifyWith(Keys.hmacShaKeyFor(secret.getBytes()))
                    .build()
                    .parseSignedClaims(token);
            return true;
        } catch (SignatureException e) {
            logger.error("无效的JWT签名: {}", e.getMessage());
        } catch (MalformedJwtException e) {
            logger.error("无效的JWT令牌: {}", e.getMessage());
        } catch (ExpiredJwtException e) {
            logger.info("JWT令牌已过期: {}，将尝试刷新", e.getMessage());

            // 令牌过期但签名有效，尝试刷新
            try {
                String username = e.getClaims().getSubject();
                if (username != null && !username.isEmpty()) {
                    logger.info("自动刷新令牌 - 从过期令牌提取用户名: {}", username);
                    return true; // 返回true以允许刷新流程继续
                }
            } catch (Exception ex) {
                logger.error("处理过期令牌时发生错误: {}", ex.getMessage());
            }
        } catch (UnsupportedJwtException e) {
            logger.error("不支持的JWT令牌: {}", e.getMessage());
        } catch (IllegalArgumentException e) {
            logger.error("JWT声明字符串为空: {}", e.getMessage());
        }
        return false;
    }

    // 特别为刷新令牌验证添加方法，允许令牌已过期
    public boolean validateTokenIgnoreExpiration(String token) {
        try {
            Jwts.parser()
                    .verifyWith(Keys.hmacShaKeyFor(secret.getBytes()))
                    .build()
                    .parseSignedClaims(token);
            return true;
        } catch (ExpiredJwtException e) {
            // 如果是因为过期而失败，我们仍然认为令牌有效（用于刷新）
            return true;
        } catch (Exception e) {
            logger.error("令牌验证失败（忽略过期）: {}", e.getMessage());
            return false;
        }
    }
}
