package com.xsh.learningtracker.service.impl;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.xsh.learningtracker.entity.Tag;
import com.xsh.learningtracker.entity.Task;
import com.xsh.learningtracker.entity.TaskTag;
import com.xsh.learningtracker.repository.TagRepository;
import com.xsh.learningtracker.repository.TaskRepository;
import com.xsh.learningtracker.repository.TaskTagRepository;
import com.xsh.learningtracker.service.TaskTagService;

import lombok.RequiredArgsConstructor;

@Service
@Transactional
@RequiredArgsConstructor
public class TaskTagServiceImpl implements TaskTagService {

    private final TaskRepository taskRepository;
    private final TagRepository tagRepository;
    private final TaskTagRepository taskTagRepository;

    @Override
    public void addTagToTask(Integer taskId, Integer tagId) {
        Task task = taskRepository.findById(taskId)
                .orElseThrow(() -> new RuntimeException("任务不存在，ID: " + taskId));

        Tag tag = tagRepository.findById(tagId)
                .orElseThrow(() -> new RuntimeException("标签不存在，ID: " + tagId));

        // 检查关系是否已经存在
        boolean relationExists = task.getTags().stream()
                .anyMatch(t -> t.getId().equals(tagId));

        if (!relationExists) {
            task.getTags().add(tag);
            taskRepository.save(task);
        }
    }

    @Override
    public void addTagsToTask(Integer taskId, List<Integer> tagIds) {
        Task task = taskRepository.findById(taskId)
                .orElseThrow(() -> new RuntimeException("任务不存在，ID: " + taskId));

        List<Tag> tagsToAdd = tagRepository.findAllById(tagIds);

        // 过滤掉已添加的标签
        Set<Integer> existingTagIds = task.getTags().stream()
                .map(Tag::getId)
                .collect(Collectors.toSet());

        List<Tag> newTags = tagsToAdd.stream()
                .filter(tag -> !existingTagIds.contains(tag.getId()))
                .collect(Collectors.toList());

        task.getTags().addAll(newTags);
        taskRepository.save(task);
    }

    @Override
    public void removeTagFromTask(Integer taskId, Integer tagId) {
        // 使用repository直接删除关联关系，无需加载实体
        taskTagRepository.deleteByTask_IdAndTag_Id(taskId, tagId);
    }

    @Override
    public Set<Tag> getTagsByTaskId(Integer taskId) {
        List<TaskTag> taskTags = taskTagRepository.findByTask_Id(taskId);
        return taskTags.stream()
                .map(TaskTag::getTag)
                .collect(Collectors.toSet());
    }

    @Override
    public List<Task> getTasksByTagId(Integer tagId) {
        List<TaskTag> taskTags = taskTagRepository.findByTag_Id(tagId);
        List<Integer> taskIds = taskTags.stream()
                .map(TaskTag::getTaskId)
                .collect(Collectors.toList());

        return taskRepository.findAllById(taskIds);
    }

    @Override
    public void setTaskTags(Integer taskId, List<Integer> tagIds) {
        Task task = taskRepository.findById(taskId)
                .orElseThrow(() -> new RuntimeException("任务不存在，ID: " + taskId));

        // 清除当前所有标签
        task.getTags().clear();

        // 添加新标签
        if (tagIds != null && !tagIds.isEmpty()) {
            List<Tag> tags = tagRepository.findAllById(tagIds);
            task.getTags().addAll(tags);
        }

        taskRepository.save(task);
    }

    @Override
    public void clearTaskTags(Integer taskId) {
        // 检查任务是否存在
        if (!taskRepository.existsById(taskId)) {
            throw new RuntimeException("任务不存在，ID: " + taskId);
        }

        // 删除所有标签关联
        taskTagRepository.deleteByTask_Id(taskId);
    }
}
