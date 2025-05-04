package com.xsh.learningtracker.dto;

import java.time.LocalDateTime;
import java.util.List;

import lombok.Data;

@Data
public class CreativeTaskDetailDTO {
    private String currentPhase; // 当前创作阶段
    private String publicationFormats; // 发布格式 (JSON格式)
    private String licenseType; // 许可证类型
    private Integer validationScore; // 评估分数
    private List<VersionDTO> versions; // 版本历史
    private List<FeedbackDTO> feedback; // 反馈记录

    @Data
    public static class VersionDTO {
        private LocalDateTime createdAt; // 创建时间
        private String snapshot; // 版本快照
        private String changes; // 变更内容
    }

    @Data
    public static class FeedbackDTO {
        private Integer userId; // 反馈用户ID
        private Integer creativityRating; // 创意评分
        private Integer logicRating; // 逻辑评分
        private String comments; // 评论内容
    }
}
