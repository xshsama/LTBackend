package com.xsh.learningtracker.entity;

import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 待办事项类
 * 用于表示每个步骤中的具体待办事项
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class TodoItem {

    private String id;
    private String content; // 待办事项内容
    private boolean completed = false; // 是否已完成
    private LocalDateTime createdAt; // 创建时间
    private LocalDateTime completedAt; // 完成时间
    private Integer priority = 0; // 优先级（0-低，1-中，2-高）
    private String notes; // 备注信息

    /**
     * 完成待办事项
     */
    public void complete() {
        this.completed = true;
        this.completedAt = LocalDateTime.now();
    }

    /**
     * 取消完成待办事项
     */
    public void uncomplete() {
        this.completed = false;
        this.completedAt = null;
    }
}
