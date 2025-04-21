package com.xsh.learningtracker.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

@Data
@Entity
@Table(name = "user_preferences")
@EqualsAndHashCode(exclude = "user")
@ToString(exclude = "user")
public class UserPreference {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @OneToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    // 主题偏好: light, dark, system
    @Column(name = "theme")
    private String theme = "system";

    // 通知设置
    @Column(name = "email_notifications")
    private boolean emailNotifications = true;

    @Column(name = "push_notifications")
    private boolean pushNotifications = true;

    // 学习提醒
    @Column(name = "daily_reminder_enabled")
    private boolean dailyReminderEnabled = false;

    @Column(name = "daily_reminder_time")
    private String dailyReminderTime = "08:00";

    // 学习目标提醒（天数）
    @Column(name = "goal_reminder_days")
    private Integer goalReminderDays = 3;

    // 周视图设置: 一周开始日 (1-7, 1代表周一)
    @Column(name = "week_start_day")
    private Integer weekStartDay = 1;

    // 语言偏好
    @Column(name = "language")
    private String language = "zh-CN";

    // 语言设置 (removed duplicate declaration)

    // 隐私设置
    @Column(name = "profile_visibility")
    private String profileVisibility = "public"; // public, friends, private

    @Column(name = "progress_visibility")
    private String progressVisibility = "public"; // public, friends, private

    // --- 新增字段以匹配前端 ---
    @Column(name = "task_reminder_frequency")
    private String taskReminderFrequency = "daily"; // daily, weekly, never

    @Column(name = "community_updates_enabled")
    private boolean communityUpdatesEnabled = true;

    @Column(name = "achievement_notifications_enabled")
    private boolean achievementNotificationsEnabled = true;

    @Column(name = "default_page")
    private String defaultPage = "dashboard"; // dashboard, objectives, courses

    @Column(name = "fixed_sidebar_enabled")
    private boolean fixedSidebarEnabled = true;
    // --- 新增字段结束 ---

    @PrePersist
    protected void onCreate() {
        if (theme == null)
            theme = "system";
        if (language == null)
            language = "zh-CN";
        if (profileVisibility == null)
            profileVisibility = "public";
        if (progressVisibility == null)
            progressVisibility = "public";
        if (dailyReminderTime == null)
            dailyReminderTime = "08:00";
        if (goalReminderDays == null)
            goalReminderDays = 3;
        if (weekStartDay == null)
            weekStartDay = 1;
        // 初始化新增字段的默认值
        if (taskReminderFrequency == null)
            taskReminderFrequency = "daily";
        if (defaultPage == null)
            defaultPage = "dashboard";
    }
}
