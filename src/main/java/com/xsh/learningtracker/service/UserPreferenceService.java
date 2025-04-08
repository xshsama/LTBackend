package com.xsh.learningtracker.service;

import com.xsh.learningtracker.dto.UserPreferenceDTO;

public interface UserPreferenceService {
    /**
     * 获取当前用户的偏好设置
     * 
     * @param username 用户名
     * @return 用户偏好设置DTO
     */
    UserPreferenceDTO getUserPreferences(String username);

    /**
     * 更新当前用户的偏好设置
     * 
     * @param username       用户名
     * @param preferencesDTO 偏好设置DTO
     * @return 更新后的用户偏好设置DTO
     */
    UserPreferenceDTO updateUserPreferences(String username, UserPreferenceDTO preferencesDTO);
}
