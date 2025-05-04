package com.xsh.learningtracker.dto;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import com.xsh.learningtracker.entity.CreativeTask;

import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 创意型任务DTO
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class CreativeTaskDTO extends TaskDTO {
    // 修改为与父类兼容的类型，使用String
    private String currentPhase; // 当前创作阶段
    // 修改为与父类兼容的类型
    private String publicationFormats; // 发布格式 (JSON格式)
    private String licenseType; // 许可证类型
    private Integer validationScore; // 评估分数（与父类保持Integer类型一致）
    private List<VersionDTO> versions; // 版本历史
    private List<FeedbackDTO> feedback; // 反馈记录

    /**
     * 版本记录DTO
     */
    @Data
    public static class VersionDTO {
        private LocalDateTime createdAt; // 创建时间
        private String snapshot; // 版本快照
        private String changes; // 变更内容
    }

    /**
     * 反馈记录DTO
     */
    @Data
    public static class FeedbackDTO {
        private Integer userId; // 反馈用户ID
        private Integer creativityRating; // 创意评分
        private Integer logicRating; // 逻辑评分
        private String comments; // 评论内容
    }

    /**
     * 更新创意任务阶段请求
     */
    @Data
    public static class UpdateCreativePhaseRequest {
        private String phase; // 新阶段
    }

    /**
     * 添加版本请求
     */
    @Data
    public static class AddVersionRequest {
        private String snapshot; // 版本快照
        private String changes; // 变更内容
    }

    /**
     * 添加反馈请求
     */
    @Data
    public static class AddFeedbackRequest {
        private Integer userId; // 反馈用户ID
        private Integer creativityRating; // 创意评分
        private Integer logicRating; // 逻辑评分
        private String comments; // 评论内容
    }
}
