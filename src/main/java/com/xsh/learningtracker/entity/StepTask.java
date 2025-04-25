package com.xsh.learningtracker.entity;

import java.util.ArrayList;
import java.util.List;

import jakarta.persistence.Column;
import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 步骤型任务类
 * 支持将任务分解为多个步骤，每个步骤可以有依赖关系和完成条件
 */
@Data
@EqualsAndHashCode(callSuper = true)
@Entity
@DiscriminatorValue("STEP")
public class StepTask extends BaseTask {

    @Column(name = "steps", columnDefinition = "TEXT")
    private String stepsJson; // 存储步骤信息的JSON字符串

    @Column(name = "completed_steps")
    private Integer completedSteps = 0;

    @Column(name = "blocked_steps")
    private Integer blockedSteps = 0;

    @Column(name = "validation_score")
    private Integer validationScore;

    /**
     * 步骤类，用于表示每个步骤的详细信息
     */
    public static class Step {
        private String id;
        private String title;
        private Double order;
        private List<String> dependencies = new ArrayList<>();
        private StepStatus status = StepStatus.PENDING;
        private CompletionRule completionRules = new CompletionRule();

        // Getters and setters
        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }

        public String getTitle() {
            return title;
        }

        public void setTitle(String title) {
            this.title = title;
        }

        public Double getOrder() {
            return order;
        }

        public void setOrder(Double order) {
            this.order = order;
        }

        public List<String> getDependencies() {
            return dependencies;
        }

        public void setDependencies(List<String> dependencies) {
            this.dependencies = dependencies;
        }

        public StepStatus getStatus() {
            return status;
        }

        public void setStatus(StepStatus status) {
            this.status = status;
        }

        public CompletionRule getCompletionRules() {
            return completionRules;
        }

        public void setCompletionRules(CompletionRule completionRules) {
            this.completionRules = completionRules;
        }
    }

    /**
     * 完成规则类，定义步骤如何被标记为完成
     */
    public static class CompletionRule {
        private CompletionType type = CompletionType.MANUAL;
        private Criteria criteria;

        // Getters and setters
        public CompletionType getType() {
            return type;
        }

        public void setType(CompletionType type) {
            this.type = type;
        }

        public Criteria getCriteria() {
            return criteria;
        }

        public void setCriteria(Criteria criteria) {
            this.criteria = criteria;
        }
    }

    /**
     * 自动完成条件类
     */
    public static class Criteria {
        private Integer minDuration; // 最短学习时长（分钟）
        private List<String> requiredFiles; // 必须提交的文件类型

        // Getters and setters
        public Integer getMinDuration() {
            return minDuration;
        }

        public void setMinDuration(Integer minDuration) {
            this.minDuration = minDuration;
        }

        public List<String> getRequiredFiles() {
            return requiredFiles;
        }

        public void setRequiredFiles(List<String> requiredFiles) {
            this.requiredFiles = requiredFiles;
        }
    }

    // 步骤状态枚举
    public enum StepStatus {
        PENDING, IN_PROGRESS, BLOCKED, DONE
    }

    // 完成类型枚举
    public enum CompletionType {
        MANUAL, AUTO_CHECK, FILE_UPLOAD
    }
}
