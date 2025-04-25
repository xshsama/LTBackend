package com.xsh.learningtracker.entity;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;

import jakarta.persistence.Column;
import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import jakarta.persistence.Transient;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 习惯型任务类
 * 支持定期重复执行的任务，如每日或每周习惯，包含连续打卡和历史记录
 */
@Data
@EqualsAndHashCode(callSuper = true)
@Entity
@DiscriminatorValue("HABIT")
public class HabitTask extends BaseTask {

    @Column(name = "frequency", nullable = false)
    private String frequency; // DAILY, WEEKLY, CUSTOM

    @Column(name = "days_of_week")
    private String daysOfWeekJson; // 以JSON格式存储，例如 [0,2,4,6] 表示周日、周二、周四、周六

    @Column(name = "custom_pattern")
    private String customPattern; // cron表达式

    @Column(name = "current_streak")
    private Integer currentStreak = 0; // 当前连续完成次数

    @Column(name = "longest_streak")
    private Integer longestStreak = 0; // 历史最长记录

    @Column(name = "last_completed")
    private LocalDate lastCompleted; // 最后一次打卡时间

    @Column(name = "checkins", columnDefinition = "TEXT")
    private String checkinsJson; // 以JSON格式存储打卡记录

    /**
     * 打卡记录类，记录每日完成情况
     */
    public static class CheckinRecord {
        private String date; // YYYY-MM-DD格式
        private CheckinStatus status;
        private String notes; // 最多140字符

        // Getters and setters
        public String getDate() {
            return date;
        }

        public void setDate(String date) {
            this.date = date;
        }

        public CheckinStatus getStatus() {
            return status;
        }

        public void setStatus(CheckinStatus status) {
            this.status = status;
        }

        public String getNotes() {
            return notes;
        }

        public void setNotes(String notes) {
            this.notes = notes;
        }
    }

    // 频率枚举
    public enum FrequencyType {
        DAILY, WEEKLY, CUSTOM
    }

    // 打卡状态枚举
    public enum CheckinStatus {
        DONE, SKIP, PARTIAL
    }

    /**
     * 获取所有打卡记录的Map
     */
    @Transient
    public Map<String, CheckinRecord> getCheckins() {
        // 实际实现时，需要从checkinsJson解析JSON成Map
        return new HashMap<>();
    }

    /**
     * 添加一条打卡记录
     */
    public void addCheckin(String date, CheckinStatus status, String notes) {
        // 实现添加打卡记录的逻辑
        // 更新checkinsJson
        // 更新currentStreak和longestStreak
        // 更新lastCompleted
    }
}
