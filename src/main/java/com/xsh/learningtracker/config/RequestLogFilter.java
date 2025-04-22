package com.xsh.learningtracker.config;

import java.io.IOException;
import java.util.Enumeration;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Component
@Order(Ordered.HIGHEST_PRECEDENCE)
public class RequestLogFilter extends OncePerRequestFilter {

    private static final Logger logger = LoggerFactory.getLogger(RequestLogFilter.class);

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {

        String requestURI = request.getRequestURI();

        // 只记录 /api/tasks 相关的请求
        if (requestURI.contains("/api/tasks")) {
            logger.info("================ 开始记录请求信息 ================");
            logger.info("请求URI: {}", requestURI);
            logger.info("请求方法: {}", request.getMethod());

            // 记录所有请求头
            logger.info("请求头信息:");
            Enumeration<String> headerNames = request.getHeaderNames();
            while (headerNames.hasMoreElements()) {
                String headerName = headerNames.nextElement();
                String headerValue = request.getHeader(headerName);
                // 对于敏感信息如Authorization头，只显示部分内容
                if ("authorization".equalsIgnoreCase(headerName) && headerValue != null && headerValue.length() > 20) {
                    logger.info("{}:{}", headerName, headerValue.substring(0, 20) + "...");
                } else {
                    logger.info("{}:{}", headerName, headerValue);
                }
            }

            // 记录客户端IP
            logger.info("客户端IP: {}", request.getRemoteAddr());
            logger.info("================ 请求信息记录结束 ================");
        }

        // 继续处理请求
        filterChain.doFilter(request, response);

        // 只记录 /api/tasks 相关的请求的响应状态
        if (requestURI.contains("/api/tasks")) {
            logger.info("响应状态码: {}", response.getStatus());
        }
    }
}
