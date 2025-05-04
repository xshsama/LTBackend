package com.xsh.learningtracker.dto;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import com.xsh.learningtracker.entity.BaseTask;
import com.xsh.learningtracker.entity.HabitTask;

import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 习惯型任务DTO
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class HabitTaskDTO extends TaskDTO {
    private String frequency; // 频率（每日、每周等）
    // 修改为与父类兼容的类型
    // 使用String类型，前端解析时可转换为列表
    private String daysOfWeek; // 每周几 (JSON格式)
    private String customPattern; // 自定义模式
    private Integer currentStreak; // 当前连续完成次数
    private Integer longestStreak; // 最长连续完成次数
    private LocalDate lastCompleted; // 最后一次完成日期
    private List<CheckInRecordDTO> checkInRecords; // 打卡记录

    /**
     * 打卡记录DTO
     */
    @Data
    public static class CheckInRecordDTO {
        private LocalDate date; // 打卡日期
        private HabitTask.CheckinStatus status; // 打卡状态
        private String notes; // 备注
    }

    /**
     * 添加打卡记录请求
     */
    @Data
    public static class AddCheckinRequest {
        private LocalDate date; // 打卡日期
        private String status; // 打卡状态
        private String notes; // 备注
    }
}
