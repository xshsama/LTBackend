package com.xsh.learningtracker.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 任务标签关联实体
 * 用于存储Task和Tag之间的多对多关系
 */
@Entity
@Table(name = "task_tags")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TaskTag {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne
    @JoinColumn(name = "task_id")
    private BaseTask task;

    @ManyToOne
    @JoinColumn(name = "tag_id")
    private Tag tag;

    // 便于服务层使用的辅助方法
    public Integer getTaskId() {
        return task != null ? task.getId() : null;
    }

    public Integer getTagId() {
        return tag != null ? tag.getId() : null;
    }
}
