package com.xsh.learningtracker.dto;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import com.xsh.learningtracker.entity.Goal;
import com.xsh.learningtracker.entity.Tag;

import lombok.Data;

@Data
public class GoalDTO {
    private Integer id;
    private String title;
    private Goal.Status status;
    private Goal.Priority priority;
    private Integer progress;
    private LocalDate completionDate;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private Integer categoryId;
    private Integer subjectId;

    // 统计信息
    private Integer totalTasks;
    private Integer completedTasks;
    private Integer remainingTasks;
    private Double completionRate;
    private List<Tag> tags;

    // 用于创建和更新的内部类
    @Data
    public static class CreateGoalRequest {
        private String title;
        private Goal.Priority priority = Goal.Priority.MEDIUM;
        private Integer categoryId;
        private Integer subjectId;
        private List<Tag> tags;
    }

    @Data
    public static class UpdateGoalRequest {
        private String title;
        private Goal.Status status;
        private Goal.Priority priority;
        private Integer categoryId;
        private List<Tag> tags;
    }

    @Data
    public static class UpdateGoalProgressRequest {
        private Integer progress;
    }

    @Data
    public static class GoalStats {
        private Integer totalTasks = 0;
        private Integer completedTasks = 0;
        private Integer remainingTasks = 0;
        private Double completionRate = 0.0;

        public void calculateStats(Integer total, Integer completed) {
            this.totalTasks = total;
            this.completedTasks = completed;
            this.remainingTasks = total - completed;
            this.completionRate = total > 0 ? (completed * 100.0) / total : 0.0;
        }
    }

    @Data
    public static class UpdateStatusRequest {
        private Goal.Status status;
    }
}
