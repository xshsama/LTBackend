package com.xsh.learningtracker.config;

import java.io.IOException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import com.xsh.learningtracker.entity.User;
import com.xsh.learningtracker.service.UserService;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;

/**
 * JWT用户ID过滤器
 * 从认证上下文中获取当前用户信息，并将用户ID设置到请求属性中
 */
@Component
@RequiredArgsConstructor
public class JwtUserIdFilter extends OncePerRequestFilter {

    private static final Logger logger = LoggerFactory.getLogger(JwtUserIdFilter.class);
    private final UserService userService;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {

        try {
            // 从SecurityContext获取认证信息
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

            if (authentication != null && authentication.isAuthenticated() &&
                    authentication.getPrincipal() instanceof UserDetails) {

                UserDetails userDetails = (UserDetails) authentication.getPrincipal();
                String username = userDetails.getUsername();

                // 通过用户名查找用户
                User user = userService.findByUsername(username);

                if (user != null) {
                    // 将用户ID设置到请求属性中
                    request.setAttribute("userId", user.getId());
                    logger.debug("已将用户ID {}设置到请求属性中", user.getId());
                }
            }
        } catch (Exception e) {
            logger.error("设置用户ID属性失败", e);
            // 不阻止过滤器链继续执行
        }

        filterChain.doFilter(request, response);
    }
}
