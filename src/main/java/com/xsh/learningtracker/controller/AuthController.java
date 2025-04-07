package com.xsh.learningtracker.controller;

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
import com.xsh.learningtracker.dto.UserDTO;
import com.xsh.learningtracker.service.UserService;
import com.xsh.learningtracker.util.JwtUtil;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/auth") // Added /api prefix for consistency
@RequiredArgsConstructor
public class AuthController {

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

    @PostMapping("/refresh-token")
    public ResponseEntity<ApiResponse<RefreshTokenResponse>> refreshToken(
            @RequestHeader("Authorization") String authHeader) {

        // 从请求头中提取令牌
        String token = authHeader.substring(7); // 去掉 "Bearer " 前缀

        // 验证令牌是否有效
        if (!jwtUtil.validateToken(token)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(ApiResponse.error(401, "无效的令牌"));
        }

        // 刷新令牌
        String newToken = jwtUtil.refreshToken(token);

        // 创建响应对象
        RefreshTokenResponse response = new RefreshTokenResponse(newToken);

        return ResponseEntity.ok(ApiResponse.success("令牌已刷新", response));
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiResponse<Void>> handleException(Exception e) {
        return ResponseEntity.badRequest()
                .body(ApiResponse.error(400, e.getMessage()));
    }
}
