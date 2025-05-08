package com.xsh.learningtracker.dto;

import java.util.List;

import com.xsh.learningtracker.entity.StepTask;

import lombok.Data;

@Data
public class StepTaskDetailDTO {
    private Integer completedSteps; // 已完成步骤数
    private Integer blockedSteps; // 被阻塞的步骤数
    private List<StepDTO> steps; // 步骤列表

    @Data
    public static class StepDTO {
        private String id; // 步骤ID
        private String title; // 步骤标题
        private String description; // 步骤描述
        private StepTask.StepStatus status; // 步骤状态
        private Integer order; // 步骤顺序
        private Integer validationScore; // 验证分数
    }
}
