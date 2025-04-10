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
import com.xsh.learningtracker.dto.UserPreferenceDTO;
import com.xsh.learningtracker.service.UserPreferenceService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/user/preferences")
@RequiredArgsConstructor
@Tag(name = "用户偏好设置", description = "用户偏好设置相关接口")
public class UserPreferenceController {

    private final UserPreferenceService userPreferenceService;

    @Operation(summary = "获取用户偏好设置")
    @GetMapping
    public ResponseEntity<ApiResponse<UserPreferenceDTO>> getUserPreferences(
            @AuthenticationPrincipal UserDetails userDetails) {
        UserPreferenceDTO preferences = userPreferenceService.getUserPreferences(userDetails.getUsername());
        return ResponseEntity.ok(ApiResponse.success("获取偏好设置成功", preferences));
    }

    @Operation(summary = "更新用户偏好设置")
    @PutMapping
    public ResponseEntity<ApiResponse<UserPreferenceDTO>> updateUserPreferences(
            @AuthenticationPrincipal UserDetails userDetails,
            @RequestBody UserPreferenceDTO preferencesDTO) {
        UserPreferenceDTO updatedPreferences = userPreferenceService.updateUserPreferences(
                userDetails.getUsername(),
                preferencesDTO);
        return ResponseEntity.ok(ApiResponse.success("更新偏好设置成功", updatedPreferences));
    }
}