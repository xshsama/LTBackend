package com.xsh.learningtracker.dto;

import lombok.Data;

@Data
public class CategoryDTO {
    private Integer id;
    private String name;
    private Integer subjectId;

    // 用于创建和更新的内部类
    @Data
    public static class CreateCategoryRequest {
        private String name;
        private Integer subjectId;
    }

    @Data
    public static class UpdateCategoryRequest {
        private String name;
    }

    @Data
    public static class CategoryStats {
        private Integer totalGoals = 0;
        private Integer totalTasks = 0;
        private Integer completedTasks = 0;
    }
}
