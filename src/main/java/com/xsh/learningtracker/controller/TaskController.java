package com.xsh.learningtracker.controller;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.temporal.TemporalAdjusters;
import java.util.ArrayList;
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

import com.fasterxml.jackson.databind.ObjectMapper;
import com.xsh.learningtracker.dto.ApiResponse;
import com.xsh.learningtracker.dto.CreativeTaskDTO;
import com.xsh.learningtracker.dto.HabitTaskDTO;
import com.xsh.learningtracker.dto.StepTaskDTO;
import com.xsh.learningtracker.dto.StepTaskDetailDTO;
import com.xsh.learningtracker.dto.TaskDTO;
import com.xsh.learningtracker.entity.BaseTask;
import com.xsh.learningtracker.entity.CreativeTask;
import com.xsh.learningtracker.entity.HabitTask;
import com.xsh.learningtracker.entity.StepTask;
import com.xsh.learningtracker.entity.TodoItem;
import com.xsh.learningtracker.service.GoalService;
import com.xsh.learningtracker.service.TaskService;
import com.xsh.learningtracker.service.UserService;
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
    private UserService userService;

    @Autowired
    private HttpServletRequest request;

    @Autowired
    private ObjectMapper objectMapper;

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
    public ResponseEntity<ApiResponse<List<TaskDTO>>> getAllTasks(
            org.springframework.security.core.Authentication authentication,
            @RequestParam(required = false) BaseTask.TaskType type,
            @RequestParam(required = false) BaseTask.Status status) {
        logRequestHeaders("getAllTasks");

        // 从认证上下文获取用户信息
        String username = authentication.getName();
        Integer userId = userService.findByUsername(username).getId();

        List<BaseTask> tasks;
        if (type != null && status != null) {
            // 按类型和状态筛选
            tasks = taskService.getTasksByUserIdOrderByCreatedAtDesc(userId)
                    .stream()
                    .filter(task -> task.getType() == type && task.getStatus() == status)
                    .collect(Collectors.toList());
        } else if (type != null) {
            // 按类型筛选
            tasks = taskService.getTasksByUserIdOrderByCreatedAtDesc(userId)
                    .stream()
                    .filter(task -> task.getType() == type)
                    .collect(Collectors.toList());
        } else if (status != null) {
            // 按状态筛选
            tasks = taskService.getTasksByUserIdOrderByCreatedAtDesc(userId)
                    .stream()
                    .filter(task -> task.getStatus() == status)
                    .collect(Collectors.toList());
        } else {
            // 获取所有任务
            tasks = taskService.getTasksByUserIdOrderByCreatedAtDesc(userId);
        }

        List<TaskDTO> taskDTOs = tasks.stream()
                .map(DTOConverter::toTaskDTO)
                .collect(Collectors.toList());
        return ResponseEntity.ok(ApiResponse.success("获取任务列表成功", taskDTOs));
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

    // ----- 日期相关查询功能 -----

    // 获取今日任务 - 基于创建日期而不是截止日期
    @GetMapping("/today")
    public ResponseEntity<ApiResponse<List<TaskDTO>>> getTodayTasks(
            org.springframework.security.core.Authentication authentication) {
        logRequestHeaders("getTodayTasks");

        // 从认证上下文获取用户信息
        String username = authentication.getName();
        Integer userId = userService.findByUsername(username).getId();
        LocalDate today = LocalDate.now();

        // 获取今天创建的任务或者状态为IN_PROGRESS的任务
        List<BaseTask> tasks = taskService.getTasksByUserIdOrderByCreatedAtDesc(userId)
                .stream()
                .filter(task -> {
                    LocalDate createdDate = task.getCreatedAt() != null ? task.getCreatedAt().toLocalDate() : null;
                    return (createdDate != null && createdDate.equals(today)) ||
                            task.getStatus() == BaseTask.Status.IN_PROGRESS;
                })
                .collect(Collectors.toList());

        List<TaskDTO> taskDTOs = tasks.stream()
                .map(DTOConverter::toTaskDTO)
                .collect(Collectors.toList());
        return ResponseEntity.ok(ApiResponse.success("获取今日任务成功", taskDTOs));
    }

    // 获取本周任务 - 基于创建日期而不是截止日期
    @GetMapping("/this-week")
    public ResponseEntity<ApiResponse<List<TaskDTO>>> getThisWeekTasks(
            org.springframework.security.core.Authentication authentication) {
        logRequestHeaders("getThisWeekTasks");

        // 从认证上下文获取用户信息
        String username = authentication.getName();
        Integer userId = userService.findByUsername(username).getId();

        LocalDate today = LocalDate.now();
        LocalDate startOfWeek = today.with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY));
        LocalDate endOfWeek = today.with(TemporalAdjusters.nextOrSame(DayOfWeek.SUNDAY));

        // 获取本周创建的任务
        List<BaseTask> tasks = taskService.getTasksByUserIdOrderByCreatedAtDesc(userId)
                .stream()
                .filter(task -> {
                    LocalDate createdDate = task.getCreatedAt() != null ? task.getCreatedAt().toLocalDate() : null;
                    return createdDate != null &&
                            !createdDate.isBefore(startOfWeek) &&
                            !createdDate.isAfter(endOfWeek);
                })
                .collect(Collectors.toList());

        List<TaskDTO> taskDTOs = tasks.stream()
                .map(DTOConverter::toTaskDTO)
                .collect(Collectors.toList());
        return ResponseEntity.ok(ApiResponse.success("获取本周任务成功", taskDTOs));
    }

    // 获取逾期任务 - 基于任务状态而不是截止日期
    @GetMapping("/overdue")
    public ResponseEntity<ApiResponse<List<TaskDTO>>> getOverdueTasks(
            org.springframework.security.core.Authentication authentication) {
        logRequestHeaders("getOverdueTasks");

        // 从认证上下文获取用户信息
        String username = authentication.getName();
        Integer userId = userService.findByUsername(username).getId();

        // 获取状态为OVERDUE的任务
        List<BaseTask> tasks = taskService.getTasksByUserIdOrderByCreatedAtDesc(userId)
                .stream()
                .filter(task -> task.getStatus() == BaseTask.Status.OVERDUE)
                .collect(Collectors.toList());

        List<TaskDTO> taskDTOs = tasks.stream()
                .map(DTOConverter::toTaskDTO)
                .collect(Collectors.toList());
        return ResponseEntity.ok(ApiResponse.success("获取逾期任务成功", taskDTOs));
    }

    // ----- 按类型获取任务API -----

    /**
     * 获取所有步骤型任务
     */
    @GetMapping("/step")
    public ResponseEntity<ApiResponse<List<StepTaskDTO>>> getAllStepTasks(
            org.springframework.security.core.Authentication authentication) {
        logRequestHeaders("getAllStepTasks");

        // 从认证上下文获取用户信息
        String username = authentication.getName();
        Integer userId = userService.findByUsername(username).getId();

        // 获取所有步骤型任务
        List<BaseTask> tasks = taskService.getTasksByUserIdOrderByCreatedAtDesc(userId)
                .stream()
                .filter(task -> task.getType() == BaseTask.TaskType.STEP)
                .collect(Collectors.toList());

        // 转换为StepTaskDTO
        List<StepTaskDTO> stepTaskDTOs = tasks.stream()
                .map(task -> (StepTask) task)
                .map(this::convertToStepTaskDTO)
                .collect(Collectors.toList());

        return ResponseEntity.ok(ApiResponse.success("获取步骤型任务成功", stepTaskDTOs));
    }

    /**
     * 获取所有习惯型任务
     */
    @GetMapping("/habit")
    public ResponseEntity<ApiResponse<List<HabitTaskDTO>>> getAllHabitTasks(
            org.springframework.security.core.Authentication authentication) {
        logRequestHeaders("getAllHabitTasks");

        // 从认证上下文获取用户信息
        String username = authentication.getName();
        Integer userId = userService.findByUsername(username).getId();

        // 获取所有习惯型任务
        List<BaseTask> tasks = taskService.getTasksByUserIdOrderByCreatedAtDesc(userId)
                .stream()
                .filter(task -> task.getType() == BaseTask.TaskType.HABIT)
                .collect(Collectors.toList());

        // 转换为HabitTaskDTO
        List<HabitTaskDTO> habitTaskDTOs = tasks.stream()
                .map(task -> (HabitTask) task)
                .map(this::convertToHabitTaskDTO)
                .collect(Collectors.toList());

        return ResponseEntity.ok(ApiResponse.success("获取习惯型任务成功", habitTaskDTOs));
    }

    /**
     * 获取所有创意型任务
     */
    @GetMapping("/creative")
    public ResponseEntity<ApiResponse<List<CreativeTaskDTO>>> getAllCreativeTasks(
            org.springframework.security.core.Authentication authentication) {
        logRequestHeaders("getAllCreativeTasks");

        // 从认证上下文获取用户信息
        String username = authentication.getName();
        Integer userId = userService.findByUsername(username).getId();

        // 获取所有创意型任务
        List<BaseTask> tasks = taskService.getTasksByUserIdOrderByCreatedAtDesc(userId)
                .stream()
                .filter(task -> task.getType() == BaseTask.TaskType.CREATIVE)
                .collect(Collectors.toList());

        // 转换为CreativeTaskDTO
        List<CreativeTaskDTO> creativeTaskDTOs = tasks.stream()
                .map(task -> (CreativeTask) task)
                .map(this::convertToCreativeTaskDTO)
                .collect(Collectors.toList());

        return ResponseEntity.ok(ApiResponse.success("获取创意型任务成功", creativeTaskDTOs));
    }

    // ----- 获取单个特定类型任务 -----

    /**
     * 获取单个步骤型任务
     */
    @GetMapping("/step/{id}")
    public ResponseEntity<ApiResponse<StepTaskDTO>> getStepTask(@PathVariable Integer id) {
        logRequestHeaders("getStepTask");

        BaseTask task = taskService.getTaskById(id);
        if (task.getType() != BaseTask.TaskType.STEP) {
            return ResponseEntity.badRequest()
                    .body(ApiResponse.error(400, "该任务不是步骤型任务"));
        }

        StepTaskDTO stepTaskDTO = convertToStepTaskDTO((StepTask) task);
        return ResponseEntity.ok(ApiResponse.success("获取步骤型任务成功", stepTaskDTO));
    }

    /**
     * 获取单个习惯型任务
     */
    @GetMapping("/habit/{id}")
    public ResponseEntity<ApiResponse<HabitTaskDTO>> getHabitTask(@PathVariable Integer id) {
        logRequestHeaders("getHabitTask");

        BaseTask task = taskService.getTaskById(id);
        if (task.getType() != BaseTask.TaskType.HABIT) {
            return ResponseEntity.badRequest()
                    .body(ApiResponse.error(400, "该任务不是习惯型任务"));
        }

        HabitTaskDTO habitTaskDTO = convertToHabitTaskDTO((HabitTask) task);
        return ResponseEntity.ok(ApiResponse.success("获取习惯型任务成功", habitTaskDTO));
    }

    /**
     * 获取单个创意型任务
     */
    @GetMapping("/creative/{id}")
    public ResponseEntity<ApiResponse<CreativeTaskDTO>> getCreativeTask(@PathVariable Integer id) {
        logRequestHeaders("getCreativeTask");

        BaseTask task = taskService.getTaskById(id);
        if (task.getType() != BaseTask.TaskType.CREATIVE) {
            return ResponseEntity.badRequest()
                    .body(ApiResponse.error(400, "该任务不是创意型任务"));
        }

        CreativeTaskDTO creativeTaskDTO = convertToCreativeTaskDTO((CreativeTask) task);
        return ResponseEntity.ok(ApiResponse.success("获取创意型任务成功", creativeTaskDTO));
    }

    // ----- DTO转换方法 -----

    /**
     * 将StepTask实体转换为StepTaskDTO
     */
    private StepTaskDTO convertToStepTaskDTO(StepTask task) {
        StepTaskDTO dto = new StepTaskDTO();
        // 复制基本属性
        dto.setId(task.getId());
        dto.setTitle(task.getTitle());
        dto.setStatus(task.getStatus());
        dto.setType(task.getType());
        dto.setCompletionDate(task.getCompletionDate());
        dto.setCreatedAt(task.getCreatedAt());
        dto.setUpdatedAt(task.getUpdatedAt());
        dto.setGoalId(task.getGoal().getId());

        // 设置步骤型任务特有属性
        dto.setCompletedSteps(task.getCompletedSteps());
        dto.setBlockedSteps(task.getBlockedSteps());

        // 解析stepsJson为步骤列表
        if (task.getStepsJson() != null && !task.getStepsJson().isEmpty()) {
            try {
                // 使用ObjectMapper将JSON字符串解析为StepTask.Step对象列表
                List<StepTask.Step> steps = objectMapper.readValue(
                        task.getStepsJson(),
                        objectMapper.getTypeFactory().constructCollectionType(List.class, StepTask.Step.class));

                // 将StepTask.Step对象转换为StepTaskDTO.StepDTO对象
                List<StepTaskDTO.StepDTO> stepDTOs = new ArrayList<>();
                for (StepTask.Step step : steps) {
                    StepTaskDTO.StepDTO stepDTO = new StepTaskDTO.StepDTO();
                    stepDTO.setId(step.getId());
                    stepDTO.setTitle(step.getTitle());
                    stepDTO.setDescription(step.getDescription());
                    stepDTO.setStatus(step.getStatus());
                    stepDTO.setOrder(step.getOrder() != null ? step.getOrder().intValue() : null);

                    // 处理待办事项列表相关属性
                    stepDTO.setAsTodoList(step.isAsTodoList());

                    // 处理todoItems - 确保设置为空数组而不是null
                    List<StepTaskDetailDTO.TodoItemDTO> todoItemDTOs = new ArrayList<>();
                    if (step.getTodoItems() != null && !step.getTodoItems().isEmpty()) {
                        for (TodoItem item : step.getTodoItems()) {
                            StepTaskDetailDTO.TodoItemDTO todoItemDTO = new StepTaskDetailDTO.TodoItemDTO();
                            todoItemDTO.setId(item.getId());
                            todoItemDTO.setContent(item.getContent());
                            todoItemDTO.setCompleted(item.isCompleted());
                            todoItemDTO.setCreatedAt(item.getCreatedAt());
                            todoItemDTO.setCompletedAt(item.getCompletedAt());
                            todoItemDTO.setPriority(item.getPriority());
                            todoItemDTO.setNotes(item.getNotes());
                            todoItemDTOs.add(todoItemDTO);
                        }
                    }
                    stepDTO.setTodoItems(todoItemDTOs); // 即使为空也设置数组

                    stepDTOs.add(stepDTO);
                }

                dto.setSteps(stepDTOs);
            } catch (Exception e) {
                logger.error("解析步骤JSON失败: " + e.getMessage(), e);
            }
        }

        return dto;
    }

    /**
     * 将HabitTask实体转换为HabitTaskDTO
     */
    private HabitTaskDTO convertToHabitTaskDTO(HabitTask task) {
        HabitTaskDTO dto = new HabitTaskDTO();
        // 复制基本属性
        dto.setId(task.getId());
        dto.setTitle(task.getTitle());
        dto.setStatus(task.getStatus());
        dto.setType(task.getType());
        dto.setCompletionDate(task.getCompletionDate());
        dto.setCreatedAt(task.getCreatedAt());
        dto.setUpdatedAt(task.getUpdatedAt());
        dto.setGoalId(task.getGoal().getId());

        // 设置习惯型任务特有属性
        dto.setFrequency(task.getFrequency());
        dto.setCurrentStreak(task.getCurrentStreak());
        dto.setLongestStreak(task.getLongestStreak());
        dto.setLastCompleted(task.getLastCompleted());

        // 这里可以添加将daysOfWeek等属性转换的逻辑
        // 省略具体实现，您可以根据实际需求完善

        return dto;
    }

    /**
     * 将CreativeTask实体转换为CreativeTaskDTO
     */
    private CreativeTaskDTO convertToCreativeTaskDTO(CreativeTask task) {
        CreativeTaskDTO dto = new CreativeTaskDTO();
        // 复制基本属性
        dto.setId(task.getId());
        dto.setTitle(task.getTitle());
        dto.setStatus(task.getStatus());
        dto.setType(task.getType());
        dto.setCompletionDate(task.getCompletionDate());
        dto.setCreatedAt(task.getCreatedAt());
        dto.setUpdatedAt(task.getUpdatedAt());
        dto.setGoalId(task.getGoal().getId());

        // 设置创意型任务特有属性
        dto.setCurrentPhase(task.getCurrentPhase() != null ? task.getCurrentPhase().name() : null);
        dto.setLicenseType(task.getLicenseType());

        // 这里可以添加将publicationFormats等属性转换的逻辑
        // 省略具体实现，您可以根据实际需求完善

        return dto;
    }

    /**
     * 更新步骤型任务的步骤信息
     * 
     * @param id          任务ID
     * @param requestBody 包含步骤JSON的请求体
     * @return 更新后的步骤型任务
     */
    @PutMapping("/{id}/update-steps")
    public ResponseEntity<ApiResponse<StepTaskDTO>> updateTaskSteps(
            @PathVariable Integer id,
            @RequestBody UpdateStepsRequest requestBody,
            org.springframework.security.core.Authentication authentication) {
        logRequestHeaders("updateTaskSteps");

        // 从认证上下文获取用户信息
        String username = authentication.getName();
        Integer userId = userService.findByUsername(username).getId();

        try {
            // 验证任务存在
            if (!taskService.existsById(id)) {
                return ResponseEntity.badRequest().body(ApiResponse.error(404, "任务不存在"));
            }

            // 获取任务
            BaseTask baseTask = taskService.getTaskById(id);

            // 验证是步骤型任务
            if (!(baseTask instanceof StepTask)) {
                return ResponseEntity.badRequest().body(ApiResponse.error(400, "不是步骤型任务"));
            }

            // 更新步骤信息
            StepTask task = (StepTask) baseTask;
            task.setStepsJson(requestBody.getStepsJson());

            // 重新计算完成的步骤数
            try {
                List<StepTask.Step> steps = objectMapper.readValue(
                        requestBody.getStepsJson(),
                        objectMapper.getTypeFactory().constructCollectionType(List.class, StepTask.Step.class));

                // 计算完成的步骤数和被阻塞的步骤数
                long completedSteps = steps.stream()
                        .filter(step -> step.getStatus() == StepTask.StepStatus.DONE ||
                                (step.isAsTodoList() && step.getTodoItems() != null &&
                                        !step.getTodoItems().isEmpty() &&
                                        step.getTodoItems().stream().allMatch(TodoItem::isCompleted)))
                        .count();

                long blockedSteps = steps.stream()
                        .filter(step -> step.getStatus() == StepTask.StepStatus.BLOCKED)
                        .count();

                // 更新步骤统计
                task.setCompletedSteps((int) completedSteps);
                task.setBlockedSteps((int) blockedSteps);

            } catch (Exception e) {
                logger.error("解析步骤JSON失败: " + e.getMessage(), e);
                // 如果解析失败，不更新完成步骤数，但仍然保存steps JSON
            }

            // 保存任务
            StepTask updatedTask = taskService.updateStepTask(id, task);

            // 转换为DTO并返回
            StepTaskDTO dto = convertToStepTaskDTO(updatedTask);
            return ResponseEntity.ok(ApiResponse.success("步骤更新成功", dto));

        } catch (Exception e) {
            logger.error("更新步骤失败: " + e.getMessage(), e);
            return ResponseEntity.badRequest().body(ApiResponse.error(500, "更新步骤失败: " + e.getMessage()));
        }
    }

    /**
     * 更新步骤请求类
     */
    public static class UpdateStepsRequest {
        private String stepsJson;

        public String getStepsJson() {
            return stepsJson;
        }

        public void setStepsJson(String stepsJson) {
            this.stepsJson = stepsJson;
        }
    }
}
