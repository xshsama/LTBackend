package com.xsh.learningtracker.entity;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.databind.ObjectMapper;

import jakarta.persistence.Column;
import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 创意型任务类
 * 支持创作过程管理，包括草稿、审阅和最终发布阶段
 */
@Data
@EqualsAndHashCode(callSuper = true)
@Entity
@DiscriminatorValue("CREATIVE")
public class CreativeTask extends BaseTask {

    @Column(name = "versions_json", columnDefinition = "TEXT")
    private String versionsJson; // 存储版本历史的JSON

    @Column(name = "reviewers_json", columnDefinition = "TEXT")
    private String reviewersJson; // 存储审阅者列表的JSON

    @Column(name = "feedbacks_json", columnDefinition = "TEXT")
    private String feedbacksJson; // 存储反馈信息的JSON

    @Column(name = "publication_formats")
    private String publicationFormats; // 发布格式，如PDF, EPUB, HTML

    @Column(name = "license_type")
    private String licenseType; // 许可证类型

    @Enumerated(EnumType.STRING)
    @Column(name = "current_phase")
    private CreativePhase currentPhase = CreativePhase.DRAFTING;

    /**
     * 版本类，用于记录创作内容的各个版本
     */
    public static class Version {
        private String versionId;
        private LocalDateTime timestamp;
        private String snapshot; // 内容快照或存储路径
        private List<String> changes = new ArrayList<>(); // 变更描述

        // Getters and setters
        public String getVersionId() {
            return versionId;
        }

        public void setVersionId(String versionId) {
            this.versionId = versionId;
        }

        public LocalDateTime getTimestamp() {
            return timestamp;
        }

        public void setTimestamp(LocalDateTime timestamp) {
            this.timestamp = timestamp;
        }

        public String getSnapshot() {
            return snapshot;
        }

        public void setSnapshot(String snapshot) {
            this.snapshot = snapshot;
        }

        public List<String> getChanges() {
            return changes;
        }

        public void setChanges(List<String> changes) {
            this.changes = changes;
        }
    }

    /**
     * 反馈类，用于存储审阅者的反馈
     */
    public static class Feedback {
        private String userId;
        private Integer creativityRating; // 1-5
        private Integer logicRating; // 1-5
        private String comments;

        // Getters and setters
        public String getUserId() {
            return userId;
        }

        public void setUserId(String userId) {
            this.userId = userId;
        }

        public Integer getCreativityRating() {
            return creativityRating;
        }

        public void setCreativityRating(Integer creativityRating) {
            this.creativityRating = creativityRating;
        }

        public Integer getLogicRating() {
            return logicRating;
        }

        public void setLogicRating(Integer logicRating) {
            this.logicRating = logicRating;
        }

        public String getComments() {
            return comments;
        }

        public void setComments(String comments) {
            this.comments = comments;
        }
    }

    // 创意任务阶段枚举
    public enum CreativePhase {
        DRAFTING, REVIEWING, FINALIZING
    }

    // 发布格式枚举
    public enum PublicationFormat {
        PDF, EPUB, HTML
    }

    // 许可证类型枚举
    public enum LicenseType {
        CC_BY, ALL_RIGHTS_RESERVED
    }

    /**
     * 计算并返回验证分数
     * 基于所有反馈的平均分计算
     */
    public Integer getValidationScore() {
        try {
            if (feedbacksJson == null || feedbacksJson.isEmpty()) {
                return null;
            }

            ObjectMapper mapper = new ObjectMapper();
            Feedback[] feedbacks = mapper.readValue(feedbacksJson, Feedback[].class);

            if (feedbacks.length == 0) {
                return null;
            }

            double total = 0;
            int count = 0;

            for (Feedback feedback : feedbacks) {
                if (feedback.getCreativityRating() != null) {
                    total += feedback.getCreativityRating();
                    count++;
                }
                if (feedback.getLogicRating() != null) {
                    total += feedback.getLogicRating();
                    count++;
                }
            }

            return count > 0 ? (int) Math.round(total / count) : null;
        } catch (Exception e) {
            return null;
        }
    }
}
