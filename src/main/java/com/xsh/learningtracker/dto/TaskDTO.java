package com.xsh.learningtracker.dto;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import com.xsh.learningtracker.entity.Task;

import lombok.Data;

@Data
public class TaskDTO {
    private Integer id;
    private String title;
    private String description;
    private Task.Status status;
    private Task.Priority priority;
    private Integer estimatedTimeMinutes;
    private Integer actualTimeMinutes;
    private LocalDate completionDate;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private Integer goalId;
    private List<String> tags;

    // 用于创建和更新的内部类
    @Data
    public static class CreateTaskRequest {
        private String title;
        private String description;
        private Task.Priority priority = Task.Priority.MEDIUM;
        private Integer estimatedTimeMinutes;
        private Integer goalId;
        private List<String> tags;
    }

    @Data
    public static class UpdateTaskRequest {
        private String title;
        private String description;
        private Task.Status status;
        private Task.Priority priority;
        private Integer estimatedTimeMinutes;
        private List<String> tags;
    }

    @Data
    public static class TaskProgress {
        private Double completionPercentage;
        private Integer totalTimeSpent;
        private Integer remainingTime;
        private Boolean isOverdue;
        private Integer daysUntilDue;

        public void calculateProgress(Integer estimatedTime, Integer actualTime, LocalDate dueDate) {
            this.totalTimeSpent = actualTime;
            this.remainingTime = estimatedTime != null ? estimatedTime - actualTime : null;
            this.completionPercentage = estimatedTime != null && estimatedTime > 0
                    ? (actualTime * 100.0) / estimatedTime
                    : null;

            LocalDate today = LocalDate.now();
            this.isOverdue = dueDate != null && today.isAfter(dueDate);
            this.daysUntilDue = dueDate != null
                    ? java.time.Period.between(today, dueDate).getDays()
                    : null;
        }
    }

    @Data
    public static class UpdateStatusRequest {
        private Task.Status status;
    }

    @Data
    public static class UpdateTimeRequest {
        private Integer actualTimeMinutes;
    }
}
