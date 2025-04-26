package com.xsh.learningtracker.controller;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.xsh.learningtracker.dto.ApiResponse;
import com.xsh.learningtracker.dto.TagDTO;
import com.xsh.learningtracker.dto.TaskTagRequestDTO;
import com.xsh.learningtracker.entity.BaseTask;
import com.xsh.learningtracker.entity.Tag;
import com.xsh.learningtracker.service.TaskTagService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/task-tags")
@RequiredArgsConstructor
public class TaskTagController {

    private final TaskTagService taskTagService;

    /**
     * 为任务添加一个标签
     */
    @PostMapping("/add-tag")
    public ResponseEntity<ApiResponse<Void>> addTagToTask(@RequestBody TaskTagRequestDTO request) {
        taskTagService.addTagToTask(request.getTaskId(), request.getTagId());
        return ResponseEntity.ok(ApiResponse.success("标签已添加到任务"));
    }

    /**
     * 为任务添加多个标签
     */
    @PostMapping("/add-tags")
    public ResponseEntity<ApiResponse<Void>> addTagsToTask(@RequestBody TaskTagRequestDTO request) {
        taskTagService.addTagsToTask(request.getTaskId(), request.getTagIds());
        return ResponseEntity.ok(ApiResponse.success("标签已添加到任务"));
    }

    /**
     * 从任务中移除标签
     */
    @DeleteMapping("/task/{taskId}/tag/{tagId}")
    public ResponseEntity<ApiResponse<Void>> removeTagFromTask(
            @PathVariable Integer taskId, @PathVariable Integer tagId) {
        taskTagService.removeTagFromTask(taskId, tagId);
        return ResponseEntity.ok(ApiResponse.success("标签已从任务中移除"));
    }

    /**
     * 获取任务的所有标签
     */
    @GetMapping("/task/{taskId}/tags")
    public ResponseEntity<ApiResponse<List<TagDTO>>> getTagsByTaskId(@PathVariable Integer taskId) {
        System.out.println("接收到获取任务标签请求，任务ID: " + taskId);
        Set<Tag> tags = taskTagService.getTagsByTaskId(taskId);

        // 将Tag实体转换为TagDTO
        List<TagDTO> tagDTOs = tags.stream()
                .map(tag -> new TagDTO(
                        tag.getId(),
                        tag.getName(),
                        tag.getColor(),
                        tag.getUser() != null ? tag.getUser().getId() : null))
                .collect(Collectors.toList());

        System.out.println("返回标签数据: " + tagDTOs);
        return ResponseEntity.ok(ApiResponse.success("获取任务标签成功", tagDTOs));
    }

    /**
     * 获取带有特定标签的所有任务
     */
    @GetMapping("/tag/{tagId}/tasks")
    public ResponseEntity<ApiResponse<List<BaseTask>>> getTasksByTagId(@PathVariable Integer tagId) {
        List<BaseTask> tasks = taskTagService.getTasksByTagId(tagId);
        return ResponseEntity.ok(ApiResponse.success("获取标签相关任务成功", tasks));
    }

    /**
     * 设置任务的标签（替换现有标签）
     */
    @PutMapping("/task/{taskId}/set-tags")
    public ResponseEntity<ApiResponse<Void>> setTaskTags(
            @PathVariable Integer taskId, @RequestBody TaskTagRequestDTO request) {
        taskTagService.setTaskTags(taskId, request.getTagIds());
        return ResponseEntity.ok(ApiResponse.success("任务标签已更新"));
    }

    /**
     * 清除任务的所有标签
     */
    @DeleteMapping("/task/{taskId}/clear-tags")
    public ResponseEntity<ApiResponse<Void>> clearTaskTags(@PathVariable Integer taskId) {
        taskTagService.clearTaskTags(taskId);
        return ResponseEntity.ok(ApiResponse.success("已清除任务的所有标签"));
    }
}
