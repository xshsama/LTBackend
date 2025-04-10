package com.xsh.learningtracker.config;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Lazy;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.xsh.learningtracker.util.JwtUtil;

import io.jsonwebtoken.ExpiredJwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Component
public class JwtAuthFilter extends OncePerRequestFilter {

    private static final Logger logger = LoggerFactory.getLogger(JwtAuthFilter.class);

    private final JwtUtil jwtUtil;
    private final CustomUserDetailsService userDetailsService;
    private final ObjectMapper objectMapper = new ObjectMapper();

    // 使用构造函数注入并添加@Lazy注解解决循环依赖
    public JwtAuthFilter(JwtUtil jwtUtil, @Lazy CustomUserDetailsService userDetailsService) {
        this.jwtUtil = jwtUtil;
        this.userDetailsService = userDetailsService;
    }

    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain) throws ServletException, IOException {

        // 排除不需要JWT验证的路径（如登录、注册、刷新令牌等）
        final String requestPath = request.getServletPath();
        if (requestPath.contains("/api/auth/login") ||
                requestPath.contains("/api/auth/register") ||
                requestPath.contains("/api/auth/refresh-token")) {
            filterChain.doFilter(request, response);
            return;
        }

        // 获取Authorization头
        final String authHeader = request.getHeader("Authorization");

        String jwt = null;
        String username = null;
        boolean tokenExpired = false;

        // 检查Authorization头是否存在且以"Bearer "开头
        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            // 提取JWT令牌
            jwt = authHeader.substring(7);
            try {
                // 从JWT中提取用户名
                username = jwtUtil.getUsernameFromToken(jwt);
            } catch (ExpiredJwtException e) {
                // 令牌过期，但我们仍然可以从过期的令牌中提取用户名
                username = e.getClaims().getSubject();
                logger.debug("JWT令牌已过期，开始自动刷新流程");
                logger.debug("从过期令牌中提取到用户名: {}", username);

                // 直接在这里生成新令牌
                try {
                    String refreshedToken = jwtUtil.generateToken(username);
                    logger.debug("成功生成新令牌");

                    // 将刷新后的令牌返回给客户端
                    response.setStatus(HttpServletResponse.SC_UNAUTHORIZED); // 仍然返回401，但附带刷新的令牌
                    response.setContentType("application/json");
                    response.setCharacterEncoding("UTF-8");

                    Map<String, Object> tokenResponse = new HashMap<>();
                    tokenResponse.put("error", "TokenExpired");
                    tokenResponse.put("message", "令牌已过期，已自动刷新");
                    tokenResponse.put("refreshedToken", refreshedToken);

                    logger.debug("返回刷新的令牌到客户端: {}",
                            refreshedToken.substring(0, Math.min(20, refreshedToken.length())) + "...");
                    response.getWriter().write(objectMapper.writeValueAsString(tokenResponse));
                    return;
                } catch (Exception ex) {
                    logger.error("刷新令牌失败: {}", ex.getMessage());
                }

                tokenExpired = true;
            } catch (Exception e) {
                logger.error("JWT令牌无效或解析失败: {}", e.getMessage());
                response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                response.setContentType("application/json");
                response.setCharacterEncoding("UTF-8");

                Map<String, Object> errorDetails = new HashMap<>();
                errorDetails.put("error", "Unauthorized");
                errorDetails.put("message", "Invalid JWT token");

                response.getWriter().write(objectMapper.writeValueAsString(errorDetails));
                return;
            }
        } else {
            logger.warn("未找到JWT令牌或格式不正确");
            filterChain.doFilter(request, response);
            return;
        }

        // 如果成功获取到用户名且当前SecurityContext中没有认证信息
        if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {
            try {
                // 如果令牌已过期，尝试刷新
                if (tokenExpired) {
                    logger.info("尝试刷新过期令牌");

                    // 使用validateTokenIgnoreExpiration方法验证令牌除过期外是否有效
                    if (jwtUtil.validateTokenIgnoreExpiration(jwt)) {
                        String refreshedToken = jwtUtil.refreshToken(jwt);
                        logger.info("令牌刷新成功");

                        // 将刷新后的令牌返回给客户端
                        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED); // 仍然返回401，但附带刷新的令牌
                        response.setContentType("application/json");
                        response.setCharacterEncoding("UTF-8");

                        Map<String, Object> tokenResponse = new HashMap<>();
                        tokenResponse.put("error", "TokenExpired");
                        tokenResponse.put("message", "令牌已过期，已自动刷新");

                        // 打印刷新令牌以便调试
                        logger.info("刷新后的令牌: {}",
                                refreshedToken.substring(0, Math.min(20, refreshedToken.length())) + "...");

                        // 简化返回结构，直接将令牌作为字符串返回
                        tokenResponse.put("refreshedToken", refreshedToken);

                        response.getWriter().write(objectMapper.writeValueAsString(tokenResponse));
                        return;
                    }
                } else {
                    // 令牌未过期，进行正常验证
                    // 加载用户详情
                    UserDetails userDetails = userDetailsService.loadUserByUsername(username);

                    // 验证JWT令牌
                    if (jwtUtil.validateToken(jwt)) {
                        // 创建认证令牌
                        UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(
                                userDetails, null, userDetails.getAuthorities());

                        // 设置认证详情
                        authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

                        // 在SecurityContext中设置认证信息
                        SecurityContextHolder.getContext().setAuthentication(authToken);
                    }
                }
            } catch (Exception e) {
                logger.error("令牌处理过程中发生错误: {}", e.getMessage());
            }
        }

        // 继续过滤器链
        filterChain.doFilter(request, response);
    }
}
