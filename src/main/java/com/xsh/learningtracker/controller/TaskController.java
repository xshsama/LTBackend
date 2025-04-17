package com.xsh.learningtracker.controller;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
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
import com.xsh.learningtracker.dto.TaskDTO;
import com.xsh.learningtracker.entity.Task;
import com.xsh.learningtracker.service.GoalService;
import com.xsh.learningtracker.service.TaskService;
import com.xsh.learningtracker.util.DTOConverter;

@RestController
@RequestMapping("/api/tasks")
public class TaskController {

    @Autowired
    private TaskService taskService;

    @Autowired
    private GoalService goalService;

    @GetMapping("/goal/{goalId}")
    public ResponseEntity<List<TaskDTO>> getTasksByGoal(@PathVariable Integer goalId) {
        List<Task> tasks = taskService.getTasksByGoalId(goalId);
        List<TaskDTO> taskDTOs = tasks.stream()
                .map(DTOConverter::toTaskDTO)
                .collect(Collectors.toList());
        return ResponseEntity.ok(taskDTOs);
    }

    @GetMapping("/{id}")
    public ResponseEntity<TaskDTO> getTask(@PathVariable Integer id) {
        Task task = taskService.getTaskById(id);
        return ResponseEntity.ok(DTOConverter.toTaskDTO(task));
    }

    @PostMapping
    public ResponseEntity<TaskDTO> createTask(@RequestBody TaskDTO.CreateTaskRequest request) {
        Task task = new Task();
        task.setTitle(request.getTitle());
        task.setDescription(request.getDescription());
        task.setPriority(request.getPriority());
        task.setGoal(goalService.getGoalById(request.getGoalId()));
        Task createdTask = taskService.createTask(task, request.getGoalId());
        return ResponseEntity.ok(DTOConverter.toTaskDTO(createdTask));
    }

    @PutMapping("/{id}")
    public ResponseEntity<TaskDTO> updateTask(
            @PathVariable Integer id,
            @RequestBody TaskDTO.UpdateTaskRequest request) {
        Task task = DTOConverter.toTask(request);
        // 更新任务时保留原有的Goal关联
        Task existingTask = taskService.getTaskById(id);
        task.setGoal(existingTask.getGoal());
        Task updatedTask = taskService.updateTask(id, task);
        return ResponseEntity.ok(DTOConverter.toTaskDTO(updatedTask));
    }

    @PutMapping("/{id}/status")
    public ResponseEntity<TaskDTO> updateTaskStatus(
            @PathVariable Integer id,
            @RequestBody TaskDTO.UpdateStatusRequest request) {
        Task updatedTask = taskService.updateTaskStatus(id, request.getStatus());
        return ResponseEntity.ok(DTOConverter.toTaskDTO(updatedTask));
    }

    @PutMapping("/{id}/time-spent")
    public ResponseEntity<TaskDTO> updateActualTime(
            @PathVariable Integer id,
            @RequestBody TaskDTO.UpdateTimeRequest request) {
        Task updatedTask = taskService.updateActualTime(id, request.getActualTimeMinutes());
        return ResponseEntity.ok(DTOConverter.toTaskDTO(updatedTask));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> deleteTask(@PathVariable Integer id) {
        taskService.deleteTask(id);
        return ResponseEntity.ok(ApiResponse.success("Task deleted successfully", null));
    }
}
