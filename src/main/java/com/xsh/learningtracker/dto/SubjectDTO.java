package com.xsh.learningtracker.dto;

import java.time.LocalDateTime;
import java.util.List;

import lombok.Data;

@Data
public class SubjectDTO {
    private Integer id;
    private String title;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    // 统计信息
    private Integer totalGoals;
    private Integer completedGoals;
    private Integer totalTasks;
    private Integer completedTasks;
    private Double completionRate;

    // 关联数据
    private CategoryDTO category;
    private List<String> tags;

    // 用于创建和更新的内部类
    @Data
    public static class CreateSubjectRequest {
        private String title;
        private Integer userId;
        private Integer categoryId;
    }

    @Data
    public static class UpdateSubjectRequest {
        private String title;
        private Integer categoryId;
    }

    @Data
    public static class SubjectStats {
        private Integer totalGoals = 0;
        private Integer completedGoals = 0;
        private Integer totalTasks = 0;
        private Integer completedTasks = 0;
        private Double completionRate = 0.0;

        public void calculateCompletionRate() {
            if (totalTasks > 0) {
                this.completionRate = (completedTasks * 100.0) / totalTasks;
            }
        }
    }
}
