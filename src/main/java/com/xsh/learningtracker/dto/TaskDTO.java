package com.xsh.learningtracker.dto;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import com.xsh.learningtracker.entity.BaseTask;

import lombok.Data;

@Data
public class TaskDTO {
    private Integer id;
    private String title;
    private BaseTask.Status status;
    private BaseTask.TaskType type; // 任务类型：STEP, HABIT, CREATIVE
    private LocalDate completionDate;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private Integer goalId;
    private List<TagDTO> tags = new ArrayList<>();
    private String metadata; // 存储扩展属性的JSON字符串

    // 特定任务类型的详细信息(组合模式)
    private StepTaskDetailDTO stepTaskDetail;
    private HabitTaskDetailDTO habitTaskDetail;
    private CreativeTaskDetailDTO creativeTaskDetail;

    // 用于创建任务的请求类
    @Data
    public static class CreateTaskRequest {
        private String title;
        private BaseTask.TaskType type = BaseTask.TaskType.STEP; // 默认为步骤型任务
        private Integer goalId;
        private List<Integer> tagIds;
        private String metadata; // JSON格式的元数据

        // 步骤型任务特有字段
        private String stepsJson;

        // 习惯型任务特有字段
        private String frequency;
        private String daysOfWeekJson;
        private String customPattern;

        // 创意型任务特有字段
        private String publicationFormats;
        private String licenseType;
    }

    // 用于更新任务的请求类
    @Data
    public static class UpdateTaskRequest {
        private String title;
        private BaseTask.Status status;
        private LocalDate completionDate;
        private List<Integer> tagIds;
        private String metadata;

        // 步骤型任务特有字段
        private String stepsJson;
        private Integer completedSteps;
        private Integer blockedSteps;

        // 习惯型任务特有字段
        private String frequency;
        private String daysOfWeekJson;
        private String customPattern;
        private String checkinsJson;

        // 创意型任务特有字段
        private String versionsJson;
        private String reviewersJson;
        private String feedbacksJson;
        private String currentPhase;
        private String publicationFormats;
        private String licenseType;
    }

    // 任务进度类
    @Data
    public static class TaskProgress {
        private Double completionPercentage;
        private Boolean isOverdue;
        private Integer daysUntilDue;

        // 步骤型任务特有
        private Integer totalSteps;
        private Integer completedSteps;
        private Integer blockedSteps;

        // 习惯型任务特有
        private Integer currentStreak;
        private Integer longestStreak;
        private Long daysActive;

        // 创意型任务特有
        private String currentPhase;
        private Integer totalVersions;
        private Integer totalFeedbacks;
        private Double averageRating;
    }

    // 更新任务状态请求
    @Data
    public static class UpdateStatusRequest {
        private BaseTask.Status status;
    }

    // 步骤型任务特有 - 更新步骤状态请求
    @Data
    public static class UpdateStepStatusRequest {
        private String stepId;
        private String status; // PENDING, IN_PROGRESS, BLOCKED, DONE
    }

    // 习惯型任务特有 - 添加打卡记录请求
    @Data
    public static class AddCheckinRequest {
        private String date; // YYYY-MM-DD格式
        private String status; // DONE, SKIP, PARTIAL
        private String notes;
    }

    // 创意型任务特有 - 更新创意任务阶段请求
    @Data
    public static class UpdateCreativePhaseRequest {
        private String phase; // DRAFTING, REVIEWING, FINALIZING
    }

    // 创意型任务特有 - 添加版本请求
    @Data
    public static class AddVersionRequest {
        private String snapshot; // 内容快照或存储路径
        private List<String> changes; // 变更描述
    }

    // 创意型任务特有 - 添加反馈请求
    @Data
    public static class AddFeedbackRequest {
        private String userId;
        private Integer creativityRating; // 1-5
        private Integer logicRating; // 1-5
        private String comments;
    }
}
