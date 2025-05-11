package com.xsh.learningtracker.config;

import java.io.IOException;

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
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.UnsupportedJwtException;
import io.jsonwebtoken.security.SignatureException; // Corrected import
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
        logger.debug("当前请求路径: {}", requestPath);

        // 检查路径是否需要跳过JWT验证
        if (requestPath.equals("/api/auth/login") ||
                requestPath.equals("/api/auth/register") ||
                requestPath.equals("/api/auth/refresh-token") ||
                requestPath.startsWith("/api/auth/") ||
                requestPath.startsWith("/api/tags/") && request.getMethod().equals("GET") ||
                requestPath.startsWith("/api/categories/") && request.getMethod().equals("GET")) {
            logger.debug("跳过JWT验证，路径: {}", requestPath);
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
                logger.debug("JWT令牌已过期，尝试刷新...");
                username = e.getClaims().getSubject();
                Integer userId = e.getClaims().get("userId", Integer.class);

                if (username != null && userId != null) {
                    logger.debug("从过期令牌中提取到用户名: {}, 用户ID: {}", username, userId);
                    try {
                        String refreshedToken = jwtUtil.generateToken(username, userId);
                        logger.info("令牌已刷新. 新令牌已在响应头 X-Refreshed-Token 中设置。");
                        response.setHeader("X-Refreshed-Token", refreshedToken);

                        // 尝试使用新令牌为当前请求设置安全上下文
                        if (jwtUtil.validateToken(refreshedToken)) {
                            UserDetails userDetails = this.userDetailsService.loadUserByUsername(username);
                            UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(
                                    userDetails, null, userDetails.getAuthorities());
                            authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                            SecurityContextHolder.getContext().setAuthentication(authentication);
                            logger.debug("已使用刷新后的令牌为当前请求设置安全上下文。");
                            jwt = refreshedToken; // 更新jwt变量，以便后续的doFilterInternal中的逻辑使用新token
                            tokenExpired = false; // 标记为不再过期，因为我们已经处理了
                        } else {
                            logger.warn("刷新后的令牌验证失败。当前请求将按原过期令牌处理。");
                            tokenExpired = true; // 保持过期状态
                        }
                    } catch (Exception refreshEx) {
                        logger.error("刷新令牌或设置安全上下文时出错: {}", refreshEx.getMessage());
                        tokenExpired = true; // 刷新失败，保持过期状态
                    }
                } else {
                    logger.warn("无法从过期的令牌中提取完整的用户信息 (username or userId is null)，无法自动刷新令牌。");
                    tokenExpired = true; // 无法刷新，保持过期状态
                }
                // 不在此处 return，让后续逻辑判断是否设置 SecurityContext
            } catch (MalformedJwtException | SignatureException | UnsupportedJwtException
                    | IllegalArgumentException ex) {
                logger.error("无效的JWT令牌 (非过期问题): {}", ex.getMessage());
                username = null; // 确保username为null
                SecurityContextHolder.clearContext(); // 清除上下文
                // 对于这些类型的错误，我们通常希望直接返回401，而不是继续过滤器链然后等待Spring Security处理。
                // 但为了与原始逻辑的结构保持一定的相似性，这里不直接return，
                // 而是依赖后续的 SecurityContextHolder.getContext().getAuthentication() == null
                // 来阻止设置认证。
                // 更严格的做法是在这里直接返回错误响应。
                // response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                // response.setContentType("application/json");
                // response.getWriter().write("{\"error\": \"Invalid JWT token\"}");
                // return;
            } catch (Exception e) { // Catch-all for other unexpected errors during token parsing
                logger.error("解析JWT时发生未知错误: {}", e.getMessage());
                username = null;
                SecurityContextHolder.clearContext();
                // response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                // response.setContentType("application/json");
                // response.getWriter().write("{\"error\": \"Token processing error\"}");
                // return;
            }
        } else {
            logger.debug("请求头中没有Bearer Token。路径: {}", requestPath);
            // 如果没有token，则username为null，后续的SecurityContextHolder.getContext().getAuthentication()
            // == null会为true，
            // 但由于username为null，不会进入设置Authentication的逻辑，最终由Spring Security处理为匿名或拒绝。
        }

        // 如果token有效（或已成功刷新并设置了上下文），并且SecurityContext中还没有Authentication
        if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {
            // 注意：如果token因非过期原因无效，上面的catch块已将username设为null，此条件不会满足。
            // 如果token过期但刷新成功且上下文已设置，此条件也会为false（因为上下文已填充）。
            // 此块主要处理首次验证有效token，或处理刷新后上下文未被立即设置的边缘情况（尽管我们尝试了透明刷新）。
            if (!tokenExpired && jwtUtil.validateToken(jwt)) { // 只有当token未标记为过期且验证通过时
                UserDetails userDetails = this.userDetailsService.loadUserByUsername(username);
                UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(
                        userDetails, null, userDetails.getAuthorities());
                authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                SecurityContextHolder.getContext().setAuthentication(authentication);
                logger.debug("已为用户 '{}' 设置安全上下文。", username);
            } else if (tokenExpired) {
                logger.debug("令牌已过期且未能成功刷新并设置上下文，用户 '{}' 的安全上下文未设置。", username);
                SecurityContextHolder.clearContext(); // 确保过期且未刷新的token不会意外通过
            } else {
                logger.debug("令牌验证失败，用户 '{}' 的安全上下文未设置。", username);
                SecurityContextHolder.clearContext();
            }
        } else if (username == null && authHeader != null && authHeader.startsWith("Bearer ")) {
            logger.debug("无法从提供的Bearer token中提取有效的用户名或令牌无效，安全上下文未设置。");
            SecurityContextHolder.clearContext();
        }

        // 如果在上面的ExpiredJwtException块中，我们已经写入了响应并返回，那么这里的doFilter不会执行。
        // 但根据当前的修改，我们不再在ExpiredJwtException中直接返回。
        // 因此，doFilter总是会被调用。Spring Security的后续过滤器（如AuthorizationFilter）
        // 将基于SecurityContextHolder中的Authentication对象来决定是否授权。
        // 如果Authentication为null（因为token无效/过期且未成功刷新），则会触发AuthenticationEntryPoint，通常返回401。
        filterChain.doFilter(request, response);
    }
}
