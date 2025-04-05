package com.xsh.learningtracker.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.xsh.learningtracker.dto.ApiResponse;
import com.xsh.learningtracker.dto.LoginRequest;
import com.xsh.learningtracker.dto.LoginResponseDTO;
import com.xsh.learningtracker.dto.RegisterRequest;
import com.xsh.learningtracker.dto.UserDTO;
import com.xsh.learningtracker.service.UserService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/auth") // Added /api prefix for consistency
@RequiredArgsConstructor
public class AuthController {

    private final UserService userService;

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

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiResponse<Void>> handleException(Exception e) {
        return ResponseEntity.badRequest()
                .body(ApiResponse.error(400, e.getMessage()));
    }
}
