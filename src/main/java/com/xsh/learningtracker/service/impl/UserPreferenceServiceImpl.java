package com.xsh.learningtracker.service.impl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.xsh.learningtracker.dto.UserPreferenceDTO;
import com.xsh.learningtracker.entity.User;
import com.xsh.learningtracker.entity.UserPreference;
import com.xsh.learningtracker.exception.UserException;
import com.xsh.learningtracker.repository.UserPreferenceRepository;
import com.xsh.learningtracker.repository.UserRepository;
import com.xsh.learningtracker.service.UserPreferenceService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class UserPreferenceServiceImpl implements UserPreferenceService {

    private static final Logger logger = LoggerFactory.getLogger(UserPreferenceServiceImpl.class);

    private final UserPreferenceRepository userPreferenceRepository;
    private final UserRepository userRepository;

    @Override
    @Transactional(readOnly = true)
    public UserPreferenceDTO getUserPreferences(String username) {
        logger.debug("正在获取用户 {} 的偏好设置", username);

        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new UserException("未找到用户：" + username));

        UserPreference preferences = userPreferenceRepository.findByUserUsername(username)
                .orElseGet(() -> {
                    logger.info("用户 {} 没有偏好设置，创建默认设置", username);
                    return createDefaultPreferences(user);
                });

        logger.debug("成功获取用户 {} 的偏好设置", username);
        return mapEntityToDto(preferences);
    }

    @Override
    @Transactional
    public UserPreferenceDTO updateUserPreferences(String username, UserPreferenceDTO preferencesDTO) {
        logger.debug("正在更新用户 {} 的偏好设置", username);
        logger.debug("更新请求数据: {}", preferencesDTO);

        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new UserException("未找到用户：" + username));

        UserPreference preferences = userPreferenceRepository.findByUserUsername(username)
                .orElseGet(() -> {
                    logger.info("用户 {} 没有偏好设置，创建默认设置", username);
                    return createDefaultPreferences(user);
                });

        updateEntityFromDto(preferences, preferencesDTO);

        UserPreference updatedPreferences = userPreferenceRepository.save(preferences);
        logger.info("已成功更新用户 {} 的偏好设置", username);

        return mapEntityToDto(updatedPreferences);
    }

    private UserPreference createDefaultPreferences(User user) {
        UserPreference preferences = new UserPreference();
        preferences.setUser(user);
        // 实体的其他字段将使用 @PrePersist 中定义的默认值
        return userPreferenceRepository.save(preferences);
    }

    private UserPreferenceDTO mapEntityToDto(UserPreference entity) {
        UserPreferenceDTO dto = new UserPreferenceDTO();

        // 基础设置
        dto.setTheme(entity.getTheme());
        dto.setLanguage(entity.getLanguage());
        dto.setDefaultPage(entity.getDefaultPage());
        dto.setFixedSidebarEnabled(entity.isFixedSidebarEnabled());

        // 通知设置
        dto.setEmailNotifications(entity.isEmailNotifications());
        dto.setTaskReminderFrequency(entity.getTaskReminderFrequency());
        dto.setCommunityUpdatesEnabled(entity.isCommunityUpdatesEnabled());
        dto.setAchievementNotificationsEnabled(entity.isAchievementNotificationsEnabled());

        // 旧字段的默认值
        dto.setShowWelcome(true);
        dto.setStatsViewMode("weekly");
        dto.setItemsPerPage(10);

        return dto;
    }

    private void updateEntityFromDto(UserPreference entity, UserPreferenceDTO dto) {
        logger.debug("正在更新用户偏好实体，DTO: {}", dto);

        // 基础设置
        if (dto.getTheme() != null) {
            entity.setTheme(dto.getTheme());
        }
        if (dto.getLanguage() != null) {
            entity.setLanguage(dto.getLanguage());
        }
        if (dto.getDefaultPage() != null) {
            entity.setDefaultPage(dto.getDefaultPage());
        }
        if (dto.getFixedSidebarEnabled() != null) {
            entity.setFixedSidebarEnabled(dto.getFixedSidebarEnabled());
        }

        // 通知设置
        if (dto.getEmailNotifications() != null) {
            entity.setEmailNotifications(dto.getEmailNotifications());
        }
        if (dto.getTaskReminderFrequency() != null) {
            entity.setTaskReminderFrequency(dto.getTaskReminderFrequency());
        }
        if (dto.getCommunityUpdatesEnabled() != null) {
            entity.setCommunityUpdatesEnabled(dto.getCommunityUpdatesEnabled());
        }
        if (dto.getAchievementNotificationsEnabled() != null) {
            entity.setAchievementNotificationsEnabled(dto.getAchievementNotificationsEnabled());
        }
    }
}
