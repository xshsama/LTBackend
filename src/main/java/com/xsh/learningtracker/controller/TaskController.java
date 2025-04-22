package com.xsh.learningtracker.controller;

import java.util.Enumeration;
import java.util.List;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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

import jakarta.servlet.http.HttpServletRequest;

@RestController
@RequestMapping("/api/tasks")
public class TaskController {

    private static final Logger logger = LoggerFactory.getLogger(TaskController.class);

    @Autowired
    private TaskService taskService;

    @Autowired
    private GoalService goalService;

    @Autowired
    private HttpServletRequest request;

    // 添加日志记录方法
    private void logRequestHeaders(String methodName) {
        logger.info("================ 开始记录 {} 请求头信息 ================", methodName);
        Enumeration<String> headerNames = request.getHeaderNames();
        while (headerNames.hasMoreElements()) {
            String headerName = headerNames.nextElement();
            String headerValue = request.getHeader(headerName);

            // 对于敏感信息如Authorization头，只显示部分内容
            if ("authorization".equalsIgnoreCase(headerName) && headerValue != null && headerValue.length() > 20) {
                logger.info("{}:{}", headerName, headerValue.substring(0, 20) + "...");
            } else {
                logger.info("{}:{}", headerName, headerValue);
            }
        }
        logger.info("请求URI: {}", request.getRequestURI());
        logger.info("请求方法: {}", request.getMethod());
        logger.info("客户端IP: {}", request.getRemoteAddr());
        logger.info("================ 记录请求头信息结束 ================");
    }

    @GetMapping("/goal/{goalId}")
    public ResponseEntity<List<TaskDTO>> getTasksByGoal(@PathVariable Integer goalId) {
        // 打印请求头信息
        logRequestHeaders("getTasksByGoal");

        List<Task> tasks = taskService.getTasksByGoalId(goalId);
        List<TaskDTO> taskDTOs = tasks.stream()
                .map(DTOConverter::toTaskDTO)
                .collect(Collectors.toList());
        return ResponseEntity.ok(taskDTOs);
    }

    @GetMapping("/{id}")
    public ResponseEntity<TaskDTO> getTask(@PathVariable Integer id) {
        // 打印请求头信息
        logRequestHeaders("getTask");

        Task task = taskService.getTaskById(id);
        return ResponseEntity.ok(DTOConverter.toTaskDTO(task));
    }

    @PostMapping
    public ResponseEntity<TaskDTO> createTask(@RequestBody TaskDTO.CreateTaskRequest request) {
        // 打印请求头信息
        logRequestHeaders("createTask");

        Task task = new Task();
        task.setTitle(request.getTitle());
        task.setPriority(request.getPriority());
        task.setGoal(goalService.getGoalById(request.getGoalId()));
        Task createdTask = taskService.createTask(task, request.getGoalId());
        return ResponseEntity.ok(DTOConverter.toTaskDTO(createdTask));
    }

    @PutMapping("/{id}")
    public ResponseEntity<TaskDTO> updateTask(
            @PathVariable Integer id,
            @RequestBody TaskDTO.UpdateTaskRequest request) {
        // 打印请求头信息
        logRequestHeaders("updateTask");

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
        // 打印请求头信息
        logRequestHeaders("updateTaskStatus");

        Task updatedTask = taskService.updateTaskStatus(id, request.getStatus());
        return ResponseEntity.ok(DTOConverter.toTaskDTO(updatedTask));
    }

    @PutMapping("/{id}/study-hours")
    public ResponseEntity<TaskDTO> updateStudyHours(
            @PathVariable Integer id,
            @RequestBody TaskDTO.UpdateStudyHoursRequest request) {
        // 打印请求头信息
        logRequestHeaders("updateStudyHours");

        Task updatedTask = taskService.updateStudyHours(id, request.getStudyHours());
        return ResponseEntity.ok(DTOConverter.toTaskDTO(updatedTask));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> deleteTask(@PathVariable Integer id) {
        // 打印请求头信息
        logRequestHeaders("deleteTask");

        taskService.deleteTask(id);
        return ResponseEntity.ok(ApiResponse.success("Task deleted successfully", null));
    }

    // 获取当前用户的所有任务
    @GetMapping
    public ResponseEntity<List<TaskDTO>> getAllTasks() {
        // 打印请求头信息
        logRequestHeaders("getAllTasks");

        // 从认证上下文获取当前用户ID
        Integer userId = (Integer) request.getAttribute("userId");
        if (userId == null) {
            logger.error("未能获取当前用户ID，可能是认证问题");
            return ResponseEntity.status(401).build();
        }

        logger.info("获取用户ID: {} 的所有任务", userId);
        List<Task> tasks = taskService.getTasksByUserIdOrderByCreatedAtDesc(userId);
        List<TaskDTO> taskDTOs = tasks.stream()
                .map(DTOConverter::toTaskDTO)
                .collect(Collectors.toList());

        logger.info("返回用户任务数量: {}", taskDTOs.size());
        return ResponseEntity.ok(taskDTOs);
    }
}
