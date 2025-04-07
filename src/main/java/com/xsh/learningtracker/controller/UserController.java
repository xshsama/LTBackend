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
import com.xsh.learningtracker.dto.UpdatePasswordRequest;
import com.xsh.learningtracker.dto.UpdateProfileRequest;
import com.xsh.learningtracker.dto.UserProfileDTO;
import com.xsh.learningtracker.service.UserService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/user")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @GetMapping("/profile")
    public ResponseEntity<ApiResponse<UserProfileDTO>> getUserProfile(
            @AuthenticationPrincipal UserDetails userDetails) {
        String username = userDetails.getUsername();
        UserProfileDTO profileDTO = userService.getUserProfile(username);

        return ResponseEntity.ok(ApiResponse.success("获取用户信息成功", profileDTO));
    }

    @PutMapping("/profile")
    public ResponseEntity<ApiResponse<UserProfileDTO>> updateProfile(
            @Valid @RequestBody UpdateProfileRequest request,
            @AuthenticationPrincipal UserDetails userDetails) {

        String username = userDetails.getUsername();
        UserProfileDTO updatedProfile = userService.updateUserProfile(username, request);

        return ResponseEntity.ok(ApiResponse.success("更新个人资料成功", updatedProfile));
    }

    @PutMapping("/password")
    public ResponseEntity<ApiResponse<String>> updatePassword(
            @Valid @RequestBody UpdatePasswordRequest request,
            @AuthenticationPrincipal UserDetails userDetails) {

        try {
            userService.updatePassword(userDetails, request);
            return ResponseEntity.ok(ApiResponse.success("密码更新成功"));// 密码更新成功
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(ApiResponse.error(200, "密码更新失败: " + e.getMessage()));
        }

    }
}
