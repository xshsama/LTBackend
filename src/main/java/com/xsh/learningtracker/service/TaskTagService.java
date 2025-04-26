package com.xsh.learningtracker.service;

import java.util.List;
import java.util.Set;

import com.xsh.learningtracker.entity.BaseTask;
import com.xsh.learningtracker.entity.Tag;

/**
 * 任务标签服务接口
 * 处理Task与Tag之间的关系
 */
public interface TaskTagService {

    /**
     * 为任务添加标签
     * 
     * @param taskId 任务ID
     * @param tagId  标签ID
     */
    void addTagToTask(Integer taskId, Integer tagId);

    /**
     * 为任务添加多个标签
     * 
     * @param taskId 任务ID
     * @param tagIds 标签ID集合
     */
    void addTagsToTask(Integer taskId, List<Integer> tagIds);

    /**
     * 从任务中移除标签
     * 
     * @param taskId 任务ID
     * @param tagId  标签ID
     */
    void removeTagFromTask(Integer taskId, Integer tagId);

    /**
     * 获取任务的所有标签
     * 
     * @param taskId 任务ID
     * @return 标签集合
     */
    Set<Tag> getTagsByTaskId(Integer taskId);

    /**
     * 获取带有特定标签的所有任务
     * 
     * @param tagId 标签ID
     * @return 任务列表
     */
    List<BaseTask> getTasksByTagId(Integer tagId);

    /**
     * 设置任务的标签（替换当前所有标签）
     * 
     * @param taskId 任务ID
     * @param tagIds 标签ID集合
     */
    void setTaskTags(Integer taskId, List<Integer> tagIds);

    /**
     * 清除任务的所有标签
     * 
     * @param taskId 任务ID
     */
    void clearTaskTags(Integer taskId);
}
