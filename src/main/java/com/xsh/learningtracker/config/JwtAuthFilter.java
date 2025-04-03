package com.xsh.learningtracker.config;

import java.io.IOException;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import com.xsh.learningtracker.util.JwtUtil;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class JwtAuthFilter extends OncePerRequestFilter {

    private final JwtUtil jwtUtil;
    private final CustomUserDetailsService userDetailsService;

    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain) throws ServletException, IOException {

        // 获取Authorization头
        final String authHeader = request.getHeader("Authorization");

        String jwt = null;
        String username = null;

        // 检查Authorization头是否存在且以"Bearer "开头
        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            // 提取JWT令牌
            jwt = authHeader.substring(7);
            try {
                // 从JWT中提取用户名
                username = jwtUtil.getUsernameFromToken(jwt);
            } catch (Exception e) {
                logger.error("无法从JWT中提取用户名", e);
            }
        }

        // 如果成功获取到用户名且当前SecurityContext中没有认证信息
        if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {
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

        // 继续过滤器链
        filterChain.doFilter(request, response);
    }
}