package com.xsh.learningtracker.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.xsh.learningtracker.dto.ApiResponse;
import com.xsh.learningtracker.dto.UpdatePasswordRequest;
import com.xsh.learningtracker.dto.UpdateProfileRequest;
import com.xsh.learningtracker.dto.UserDTO;
import com.xsh.learningtracker.dto.UserProfileDTO;
import com.xsh.learningtracker.entity.User;
import com.xsh.learningtracker.service.UserService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/user")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    /**
     * 获取当前登录用户的基本信息，包括用户ID
     * 前端可以通过此方法获取用户ID，避免在需要ID的地方使用用户名导致类型转换错误
     *
     * @param userDetails 当前登录用户信息
     * @return 包含用户ID和其他基本信息的响应
     */
    @GetMapping
    public ResponseEntity<ApiResponse<UserDTO>> getUser(@AuthenticationPrincipal UserDetails userDetails) {
        UserDTO userDTO = new UserDTO();
        if (userDetails != null) {
            String username = userDetails.getUsername();
            User user = userService.findByUsername(username);
            if (user != null) {
                userDTO.setId(user.getId());
                userDTO.setUsername(user.getUsername());
                userDTO.setCreatedAt(user.getCreatedAt());
                // 如果有其他需要的字段也可以在这里设置
            }
        } else {
            return ResponseEntity.ok(ApiResponse.success("用户未登录", null));
        }

        return ResponseEntity.ok(ApiResponse.success("获取用户信息成功", userDTO));
    }

    public String getMethodName(@RequestParam String param) {
        return new String();
    }

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
