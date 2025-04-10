package com.xsh.learningtracker.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.xsh.learningtracker.dto.ApiResponse;
import com.xsh.learningtracker.dto.LoginRequest;
import com.xsh.learningtracker.dto.LoginResponseDTO;
import com.xsh.learningtracker.dto.RefreshTokenResponse;
import com.xsh.learningtracker.dto.RegisterRequest;
import com.xsh.learningtracker.dto.TokenRefreshRequest;
import com.xsh.learningtracker.dto.UserDTO;
import com.xsh.learningtracker.exception.TokenRefreshException;
import com.xsh.learningtracker.service.UserService;
import com.xsh.learningtracker.util.JwtUtil;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/auth") // Added /api prefix for consistency
@RequiredArgsConstructor
public class AuthController {

    private static final Logger logger = LoggerFactory.getLogger(AuthController.class);

    private final UserService userService;
    private final JwtUtil jwtUtil;

    @PostMapping("/login")
    public ResponseEntity<ApiResponse<LoginResponseDTO>> login(@Valid @RequestBody LoginRequest loginRequest) {
        // 调用 userService.login 获取登录响应（包含令牌和用户信息）
        LoginResponseDTO loginResponse = userService.login(loginRequest);

        // 返回成功响应，包含登录响应数据
        return ResponseEntity.ok(ApiResponse.success("登录成功", loginResponse));
    }

    @PostMapping("/register")
    public ResponseEntity<ApiResponse<UserDTO>> register(@Valid @RequestBody RegisterRequest registerRequest) {
        UserDTO userDTO = userService.register(registerRequest);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("注册成功", userDTO));
    }

    /**
     * 令牌刷新端点 - 支持两种方式:
     * 1. 通过Authorization请求头传递令牌
     * 2. 通过请求体传递令牌（适用于某些不便于修改请求头的场景）
     */
    @PostMapping("/refresh-token")
    public ResponseEntity<ApiResponse<RefreshTokenResponse>> refreshToken(
            @RequestHeader(value = "Authorization", required = false) String authHeader,
            @RequestBody(required = false) TokenRefreshRequest refreshRequest) {

        String token = null;

        // 尝试从请求头中提取令牌
        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            token = authHeader.substring(7); // 去掉 "Bearer " 前缀
            logger.debug("从请求头中提取令牌: {}", token);
        }
        // 如果请求头中没有令牌，尝试从请求体中提取
        else if (refreshRequest != null && refreshRequest.getToken() != null) {
            token = refreshRequest.getToken();
            logger.debug("从请求体中提取令牌: {}", token);
        }

        // 如果无法获取令牌，返回错误
        if (token == null) {
            logger.error("刷新令牌请求中未提供有效令牌");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(ApiResponse.error(400, "未提供令牌"));
        }

        try {
            // 验证令牌是否有效
            if (!jwtUtil.validateToken(token)) {
                logger.error("提供的令牌无效: {}", token);
                throw new TokenRefreshException(token.substring(0, 10) + "...", "无效的令牌");
            }

            // 获取令牌中的用户名
            String username = jwtUtil.getUsernameFromToken(token);
            logger.info("正在为用户 {} 刷新令牌", username);

            // 刷新令牌
            String newToken = jwtUtil.refreshToken(token);
            logger.info("令牌刷新成功");

            // 创建响应对象
            RefreshTokenResponse response = new RefreshTokenResponse(newToken);

            return ResponseEntity.ok(ApiResponse.success("令牌已刷新", response));
        } catch (TokenRefreshException e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(ApiResponse.error(403, e.getMessage()));
        } catch (Exception e) {
            logger.error("令牌刷新过程中发生错误", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error(500, "令牌刷新失败: " + e.getMessage()));
        }
    }

    @ExceptionHandler(TokenRefreshException.class)
    public ResponseEntity<ApiResponse<Void>> handleTokenRefreshException(TokenRefreshException e) {
        return ResponseEntity.status(HttpStatus.FORBIDDEN)
                .body(ApiResponse.error(403, e.getMessage()));
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiResponse<Void>> handleException(Exception e) {
        logger.error("处理请求时发生异常", e);
        return ResponseEntity.badRequest()
                .body(ApiResponse.error(400, e.getMessage()));
    }
}
