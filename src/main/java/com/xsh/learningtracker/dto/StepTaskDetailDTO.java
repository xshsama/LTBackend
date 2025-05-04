package com.xsh.learningtracker.dto;

import java.time.LocalDateTime;
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
        private boolean asTodoList; // 是否作为待办事项列表
        private List<TodoItemDTO> todoItems; // 待办事项列表
    }

    @Data
    public static class TodoItemDTO {
        private String id; // 待办事项ID
        private String content; // 待办事项内容
        private boolean completed; // 是否已完成
        private LocalDateTime createdAt; // 创建时间
        private LocalDateTime completedAt; // 完成时间
        private Integer priority; // 优先级（0-低，1-中，2-高）
        private String notes; // 备注信息
    }
}
