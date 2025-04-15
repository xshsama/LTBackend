package com.xsh.learningtracker.dto;

import lombok.Data;

@Data
public class Subject_CategoryDTO {
    private Integer id;
    private Integer subjectId;
    private Integer categoryId;

    // 用于创建和更新的内部类
    @Data
    public static class CreateSubject_CategoryRequest {
        private Integer subjectId;
        private Integer categoryId;
    }

    @Data
    public static class UpdateSubject_CategoryRequest {
        private Integer subjectId;
        private Integer categoryId;
    }
}
