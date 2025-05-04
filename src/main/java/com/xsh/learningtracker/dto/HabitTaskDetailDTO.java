package com.xsh.learningtracker.dto;

import java.time.LocalDate;
import java.util.List;

import com.xsh.learningtracker.entity.HabitTask;

import lombok.Data;

@Data
public class HabitTaskDetailDTO {
    private String frequency; // 频率（每日、每周等）
    private String daysOfWeek; // 每周几 (JSON格式)
    private String customPattern; // 自定义模式
    private Integer currentStreak; // 当前连续完成次数
    private Integer longestStreak; // 最长连续完成次数
    private LocalDate lastCompleted; // 最后一次完成日期
    private List<CheckInRecordDTO> checkInRecords; // 打卡记录

    @Data
    public static class CheckInRecordDTO {
        private LocalDate date; // 打卡日期
        private HabitTask.CheckinStatus status; // 打卡状态
        private String notes; // 备注
    }
}
