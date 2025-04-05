package com.xsh.learningtracker.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.xsh.learningtracker.dto.ApiResponse;
import com.xsh.learningtracker.dto.UpdateProfileRequest;
import com.xsh.learningtracker.dto.UserProfileDTO;
import com.xsh.learningtracker.service.UserService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

/**
 * 用户个人资料控制器
 */
@RestController
@RequestMapping("/api/profile")
@RequiredArgsConstructor
public class UserProfileController {

    private final UserService userService;

    /**
     * 获取当前登录用户的个人资料
     * 
     * @param userDetails 当前登录用户信息
     * @return 用户个人资料
     */
    @GetMapping
    public ResponseEntity<ApiResponse<UserProfileDTO>> getProfile(
            @AuthenticationPrincipal UserDetails userDetails) {

        String username = userDetails.getUsername();
        UserProfileDTO profile = userService.getUserProfile(username);

        return ResponseEntity.ok(ApiResponse.success("获取个人资料成功", profile));
    }

    /**
     * 更新当前登录用户的个人资料
     * 
     * @param request     更新请求
     * @param userDetails 当前登录用户信息
     * @return 更新后的个人资料
     */
    @PutMapping
    public ResponseEntity<ApiResponse<UserProfileDTO>> updateProfile(
            @Valid @RequestBody UpdateProfileRequest request,
            @AuthenticationPrincipal UserDetails userDetails) {

        String username = userDetails.getUsername();
        UserProfileDTO updatedProfile = userService.updateUserProfile(username, request);

        return ResponseEntity.ok(ApiResponse.success("更新个人资料成功", updatedProfile));
    }
}