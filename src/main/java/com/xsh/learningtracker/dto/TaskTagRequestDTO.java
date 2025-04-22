package com.xsh.learningtracker.dto;

import java.util.List;

import lombok.Data;

/**
 * 任务标签请求DTO
 * 用于前端请求添加/管理任务标签的数据传输对象
 */
@Data
public class TaskTagRequestDTO {
    /**
     * 任务ID
     */
    private Integer taskId;

    /**
     * 单个标签ID（用于添加/删除单个标签）
     */
    private Integer tagId;

    /**
     * 多个标签ID列表（用于批量添加/设置标签）
     */
    private List<Integer> tagIds;
}
