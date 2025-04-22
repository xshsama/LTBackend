package com.xsh.learningtracker.dto;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import com.xsh.learningtracker.entity.Task;

import lombok.Data;

@Data
public class TaskDTO {
    private Integer id;
    private String title;
    private Task.Status status;
    private Task.Priority priority;
    private Integer studyHours; // 替换为学时字段
    private LocalDate completionDate;
    private Integer weight; // 添加权重字段，范围1-10
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private Integer goalId;
    private List<TagDTO> tags = new ArrayList<>(); // 初始化为空列表

    // 用于创建和更新的内部类
    @Data
    public static class CreateTaskRequest {
        private String title;
        private Task.Priority priority = Task.Priority.MEDIUM;
        private Integer studyHours = 0; // 替换为学时字段
        private Integer weight = 5; // 默认权重为5
        private Integer goalId;
        private List<Integer> tagIds; // 使用标签ID列表
    }

    @Data
    public static class UpdateTaskRequest {
        private String title;
        private Task.Status status;
        private Task.Priority priority;
        private Integer studyHours; // 替换为学时字段
        private Integer weight;
        private LocalDate completionDate; // 添加完成日期
        private List<Integer> tagIds; // 使用标签ID列表
    }

    @Data
    public static class TaskProgress {
        private Double completionPercentage;
        private Integer studyHours;
        private Boolean isOverdue;
        private Integer daysUntilDue;

        public void calculateProgress(Integer studyHours, LocalDate dueDate) {
            this.studyHours = studyHours;

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
    public static class UpdateStudyHoursRequest {
        private Integer studyHours;
    }
}
