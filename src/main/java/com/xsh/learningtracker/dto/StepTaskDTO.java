package com.xsh.learningtracker.dto;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import com.xsh.learningtracker.entity.BaseTask;
import com.xsh.learningtracker.entity.StepTask;

import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 步骤型任务DTO
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class StepTaskDTO extends TaskDTO {
    private Integer completedSteps; // 已完成步骤数
    private Integer blockedSteps; // 被阻塞的步骤数
    private List<StepDTO> steps; // 步骤列表

    /**
     * 步骤DTO
     */
    @Data
    public static class StepDTO {
        private String id; // 步骤ID
        private String title; // 步骤标题
        private String description; // 步骤描述
        private StepTask.StepStatus status; // 步骤状态
        private Integer order; // 步骤顺序
        private boolean asTodoList; // 是否作为待办事项列表
        private List<StepTaskDetailDTO.TodoItemDTO> todoItems; // 待办事项列表
    }

    /**
     * 更新步骤状态请求
     */
    @Data
    public static class UpdateStepStatusRequest {
        private String status; // 步骤新状态
    }
}
