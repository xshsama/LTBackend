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
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.xsh.learningtracker.dto.ApiResponse;
import com.xsh.learningtracker.dto.TaskDTO;
import com.xsh.learningtracker.entity.BaseTask;
import com.xsh.learningtracker.entity.CreativeTask;
import com.xsh.learningtracker.entity.HabitTask;
import com.xsh.learningtracker.entity.StepTask;
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

    // 根据目标ID获取任务列表
    @GetMapping("/goal/{goalId}")
    public ResponseEntity<List<TaskDTO>> getTasksByGoal(@PathVariable Integer goalId) {
        logRequestHeaders("getTasksByGoal");

        List<BaseTask> tasks = taskService.getTasksByGoalId(goalId);
        List<TaskDTO> taskDTOs = tasks.stream()
                .map(DTOConverter::toTaskDTO)
                .collect(Collectors.toList());
        return ResponseEntity.ok(taskDTOs);
    }

    // 根据任务类型和目标ID获取任务列表
    @GetMapping("/goal/{goalId}/type/{type}")
    public ResponseEntity<List<TaskDTO>> getTasksByGoalAndType(
            @PathVariable Integer goalId,
            @PathVariable BaseTask.TaskType type) {
        logRequestHeaders("getTasksByGoalAndType");

        List<BaseTask> tasks = taskService.getTasksByGoalIdAndType(goalId, type);
        List<TaskDTO> taskDTOs = tasks.stream()
                .map(DTOConverter::toTaskDTO)
                .collect(Collectors.toList());
        return ResponseEntity.ok(taskDTOs);
    }

    // 获取单个任务
    @GetMapping("/{id}")
    public ResponseEntity<TaskDTO> getTask(@PathVariable Integer id) {
        logRequestHeaders("getTask");

        BaseTask task = taskService.getTaskById(id);
        return ResponseEntity.ok(DTOConverter.toTaskDTO(task));
    }

    // 创建通用任务（任何类型）
    @PostMapping
    public ResponseEntity<TaskDTO> createTask(@RequestBody TaskDTO.CreateTaskRequest request) {
        logRequestHeaders("createTask");

        BaseTask task = DTOConverter.toBaseTask(request, goalService.getGoalById(request.getGoalId()));

        BaseTask createdTask;
        switch (request.getType()) {
            case STEP:
                createdTask = taskService.createStepTask((StepTask) task, request.getGoalId());
                break;
            case HABIT:
                createdTask = taskService.createHabitTask((HabitTask) task, request.getGoalId());
                break;
            case CREATIVE:
                createdTask = taskService.createCreativeTask((CreativeTask) task, request.getGoalId());
                break;
            default:
                createdTask = taskService.createBaseTask(task, request.getGoalId());
                break;
        }

        return ResponseEntity.ok(DTOConverter.toTaskDTO(createdTask));
    }

    // 创建步骤型任务（专用接口）
    @PostMapping("/step")
    public ResponseEntity<TaskDTO> createStepTask(@RequestBody TaskDTO.CreateTaskRequest request) {
        logRequestHeaders("createStepTask");
        request.setType(BaseTask.TaskType.STEP);

        StepTask task = (StepTask) DTOConverter.toBaseTask(request, goalService.getGoalById(request.getGoalId()));
        StepTask createdTask = taskService.createStepTask(task, request.getGoalId());

        return ResponseEntity.ok(DTOConverter.toTaskDTO(createdTask));
    }

    // 创建习惯型任务（专用接口）
    @PostMapping("/habit")
    public ResponseEntity<TaskDTO> createHabitTask(@RequestBody TaskDTO.CreateTaskRequest request) {
        logRequestHeaders("createHabitTask");
        request.setType(BaseTask.TaskType.HABIT);

        HabitTask task = (HabitTask) DTOConverter.toBaseTask(request, goalService.getGoalById(request.getGoalId()));
        HabitTask createdTask = taskService.createHabitTask(task, request.getGoalId());

        return ResponseEntity.ok(DTOConverter.toTaskDTO(createdTask));
    }

    // 创建创意型任务（专用接口）
    @PostMapping("/creative")
    public ResponseEntity<TaskDTO> createCreativeTask(@RequestBody TaskDTO.CreateTaskRequest request) {
        logRequestHeaders("createCreativeTask");
        request.setType(BaseTask.TaskType.CREATIVE);

        CreativeTask task = (CreativeTask) DTOConverter.toBaseTask(request,
                goalService.getGoalById(request.getGoalId()));
        CreativeTask createdTask = taskService.createCreativeTask(task, request.getGoalId());

        return ResponseEntity.ok(DTOConverter.toTaskDTO(createdTask));
    }

    // 更新任务
    @PutMapping("/{id}")
    public ResponseEntity<TaskDTO> updateTask(
            @PathVariable Integer id,
            @RequestBody TaskDTO.UpdateTaskRequest request) {
        logRequestHeaders("updateTask");

        BaseTask existingTask = taskService.getTaskById(id);
        BaseTask taskToUpdate = DTOConverter.toBaseTask(request, existingTask);

        BaseTask updatedTask;
        if (existingTask instanceof StepTask) {
            updatedTask = taskService.updateStepTask(id, (StepTask) taskToUpdate);
        } else if (existingTask instanceof HabitTask) {
            updatedTask = taskService.updateHabitTask(id, (HabitTask) taskToUpdate);
        } else if (existingTask instanceof CreativeTask) {
            updatedTask = taskService.updateCreativeTask(id, (CreativeTask) taskToUpdate);
        } else {
            updatedTask = taskService.updateBaseTask(id, taskToUpdate);
        }

        return ResponseEntity.ok(DTOConverter.toTaskDTO(updatedTask));
    }

    // 更新任务状态
    @PutMapping("/{id}/status")
    public ResponseEntity<TaskDTO> updateTaskStatus(
            @PathVariable Integer id,
            @RequestBody TaskDTO.UpdateStatusRequest request) {
        logRequestHeaders("updateTaskStatus");

        BaseTask updatedTask = taskService.updateTaskStatus(id, request.getStatus());
        return ResponseEntity.ok(DTOConverter.toTaskDTO(updatedTask));
    }

    // 删除任务
    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> deleteTask(@PathVariable Integer id) {
        logRequestHeaders("deleteTask");

        taskService.deleteTask(id);
        return ResponseEntity.ok(ApiResponse.success("Task deleted successfully", null));
    }

    // 获取任务列表（可按类型筛选）
    @GetMapping
    public ResponseEntity<List<TaskDTO>> getAllTasks(
            @RequestParam(required = false) BaseTask.TaskType type) {
        logRequestHeaders("getAllTasks");

        List<BaseTask> tasks;
        if (type != null) {
            tasks = taskService.getTasksByType(type);
        } else {
            // 获取当前用户的所有任务
            // 这里需要从认证上下文获取用户ID
            // 暂时假设从token中获取用户ID的逻辑已经实现
            Integer userId = 1; // 这里应该从认证上下文获取
            tasks = taskService.getTasksByUserIdOrderByCreatedAtDesc(userId);
        }

        List<TaskDTO> taskDTOs = tasks.stream()
                .map(DTOConverter::toTaskDTO)
                .collect(Collectors.toList());
        return ResponseEntity.ok(taskDTOs);
    }

    // ----- 步骤型任务特有接口 -----

    // 更新步骤状态
    @PutMapping("/{id}/step/{stepId}/status")
    public ResponseEntity<TaskDTO> updateStepStatus(
            @PathVariable Integer id,
            @PathVariable String stepId,
            @RequestBody TaskDTO.UpdateStepStatusRequest request) {
        logRequestHeaders("updateStepStatus");

        StepTask updatedTask = taskService.updateStepStatus(id, stepId,
                StepTask.StepStatus.valueOf(request.getStatus()));
        return ResponseEntity.ok(DTOConverter.toTaskDTO(updatedTask));
    }

    // 更新步骤进度
    @PutMapping("/{id}/progress")
    public ResponseEntity<TaskDTO> updateStepTaskProgress(
            @PathVariable Integer id,
            @RequestBody TaskDTO.TaskProgress request) {
        logRequestHeaders("updateStepTaskProgress");

        StepTask updatedTask = taskService.updateStepTaskProgress(
                id,
                request.getCompletedSteps(),
                request.getBlockedSteps());
        return ResponseEntity.ok(DTOConverter.toTaskDTO(updatedTask));
    }

    // ----- 习惯型任务特有接口 -----

    // 添加打卡记录
    @PostMapping("/{id}/habit/checkin")
    public ResponseEntity<TaskDTO> addCheckin(
            @PathVariable Integer id,
            @RequestBody TaskDTO.AddCheckinRequest request) {
        logRequestHeaders("addCheckin");

        HabitTask updatedTask = taskService.addCheckin(
                id,
                request.getDate(),
                HabitTask.CheckinStatus.valueOf(request.getStatus()),
                request.getNotes());
        return ResponseEntity.ok(DTOConverter.toTaskDTO(updatedTask));
    }

    // 更新连续记录
    @PutMapping("/{id}/habit/streak")
    public ResponseEntity<TaskDTO> updateStreakInfo(@PathVariable Integer id) {
        logRequestHeaders("updateStreakInfo");

        HabitTask updatedTask = taskService.updateStreakInfo(id);
        return ResponseEntity.ok(DTOConverter.toTaskDTO(updatedTask));
    }

    // ----- 创意型任务特有接口 -----

    // 更新创意任务阶段
    @PutMapping("/{id}/creative/phase")
    public ResponseEntity<TaskDTO> updateCreativePhase(
            @PathVariable Integer id,
            @RequestBody TaskDTO.UpdateCreativePhaseRequest request) {
        logRequestHeaders("updateCreativePhase");

        CreativeTask updatedTask = taskService.updateCreativePhase(
                id,
                CreativeTask.CreativePhase.valueOf(request.getPhase()));
        return ResponseEntity.ok(DTOConverter.toTaskDTO(updatedTask));
    }

    // 添加版本
    @PostMapping("/{id}/creative/version")
    public ResponseEntity<TaskDTO> addVersion(
            @PathVariable Integer id,
            @RequestBody TaskDTO.AddVersionRequest request) {
        logRequestHeaders("addVersion");

        CreativeTask.Version version = new CreativeTask.Version();
        version.setSnapshot(request.getSnapshot());
        version.setChanges(request.getChanges());

        CreativeTask updatedTask = taskService.addVersion(id, version);
        return ResponseEntity.ok(DTOConverter.toTaskDTO(updatedTask));
    }

    // 添加反馈
    @PostMapping("/{id}/creative/feedback")
    public ResponseEntity<TaskDTO> addFeedback(
            @PathVariable Integer id,
            @RequestBody TaskDTO.AddFeedbackRequest request) {
        logRequestHeaders("addFeedback");

        CreativeTask.Feedback feedback = new CreativeTask.Feedback();
        feedback.setUserId(request.getUserId());
        feedback.setCreativityRating(request.getCreativityRating());
        feedback.setLogicRating(request.getLogicRating());
        feedback.setComments(request.getComments());

        CreativeTask updatedTask = taskService.addFeedback(id, feedback);
        return ResponseEntity.ok(DTOConverter.toTaskDTO(updatedTask));
    }
}
