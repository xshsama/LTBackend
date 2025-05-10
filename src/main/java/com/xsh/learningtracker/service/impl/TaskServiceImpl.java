package com.xsh.learningtracker.service.impl;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter; // Added for date formatting
import java.util.HashMap; // Added for check-in map
import java.util.List;
import java.util.Map; // Added for check-in map

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference; // Added for map deserialization
import com.fasterxml.jackson.databind.ObjectMapper;
import com.xsh.learningtracker.entity.BaseTask;
import com.xsh.learningtracker.entity.CreativeTask;
import com.xsh.learningtracker.entity.Goal;
import com.xsh.learningtracker.entity.HabitTask;
import com.xsh.learningtracker.entity.StepTask;
import com.xsh.learningtracker.repository.GoalRepository;
import com.xsh.learningtracker.repository.TaskRepository;
import com.xsh.learningtracker.service.TaskService;

import lombok.extern.slf4j.Slf4j; // Added for logging

@Service
@Transactional
@Slf4j // Added for logging
public class TaskServiceImpl implements TaskService {

    @Autowired
    private TaskRepository taskRepository;

    @Autowired
    private GoalRepository goalRepository;

    @Autowired
    private ObjectMapper objectMapper;

    // 通用方法 - 适用于所有任务类型
    @Override
    public BaseTask createBaseTask(BaseTask task, Integer goalId) {
        Goal goal = goalRepository.findById(goalId)
                .orElseThrow(() -> new RuntimeException("Goal not found with id: " + goalId));
        task.setGoal(goal);
        if (task.getStatus() == null) {
            task.setStatus(BaseTask.Status.ACTIVE);
        }
        return taskRepository.save(task);
    }

    @Override
    public BaseTask updateBaseTask(Integer id, BaseTask taskDetails) {
        BaseTask task = getTaskById(id);
        task.setTitle(taskDetails.getTitle());
        task.setStatus(taskDetails.getStatus());
        task.setCompletionDate(taskDetails.getCompletionDate());
        if (taskDetails.getTags() != null && !taskDetails.getTags().isEmpty()) {
            task.setTags(taskDetails.getTags());
        }
        return taskRepository.save(task);
    }

    @Override
    public void deleteTask(Integer id) {
        BaseTask task = getTaskById(id);
        taskRepository.delete(task);
    }

    @Override
    public BaseTask getTaskById(Integer id) {
        return taskRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Task not found with id: " + id));
    }

    @Override
    public List<BaseTask> getTasksByGoal(Goal goal) {
        return taskRepository.findByGoal(goal);
    }

    @Override
    public List<BaseTask> getTasksByGoalId(Integer goalId) {
        return taskRepository.findByGoalId(goalId);
    }

    @Override
    public List<BaseTask> getTasksByGoalIdAndStatus(Integer goalId, BaseTask.Status status) {
        return taskRepository.findByGoalIdAndStatus(goalId, status);
    }

    @Override
    public List<BaseTask> getTasksByUserIdOrderByCreatedAtDesc(Integer userId) {
        return taskRepository.findByGoalSubjectUserIdOrderByCreatedAtDesc(userId);
    }

    @Override
    public List<BaseTask> getTasksByUserIdAndStatus(Integer userId, BaseTask.Status status) {
        return taskRepository.findByGoalSubjectUserIdAndStatus(userId, status);
    }

    @Override
    public boolean existsById(Integer id) {
        return taskRepository.existsById(id);
    }

    @Override
    public BaseTask updateTaskStatus(Integer id, BaseTask.Status status) {
        BaseTask task = getTaskById(id);
        task.setStatus(status);

        // 如果任务状态变为ARCHIVED，设置完成日期
        if (status == BaseTask.Status.ARCHIVED) {
            task.setCompletionDate(LocalDate.now());
        }

        return taskRepository.save(task);
    }

    // 步骤型任务特有方法
    @Override
    public StepTask createStepTask(StepTask task, Integer goalId) {
        Goal goal = goalRepository.findById(goalId)
                .orElseThrow(() -> new RuntimeException("Goal not found with id: " + goalId));
        task.setGoal(goal);
        task.setType(BaseTask.TaskType.STEP);
        if (task.getStatus() == null) {
            task.setStatus(BaseTask.Status.ACTIVE);
        }
        return (StepTask) taskRepository.save(task);
    }

    @Override
    public StepTask updateStepTask(Integer id, StepTask taskDetails) {
        BaseTask baseTask = getTaskById(id);
        if (!(baseTask instanceof StepTask)) {
            throw new RuntimeException("Task with id: " + id + " is not a StepTask");
        }

        StepTask task = (StepTask) baseTask;
        task.setTitle(taskDetails.getTitle());
        task.setStatus(taskDetails.getStatus());
        task.setCompletionDate(taskDetails.getCompletionDate());
        task.setStepsJson(taskDetails.getStepsJson());
        task.setCompletedSteps(taskDetails.getCompletedSteps());
        task.setBlockedSteps(taskDetails.getBlockedSteps());

        if (taskDetails.getTags() != null && !taskDetails.getTags().isEmpty()) {
            task.setTags(taskDetails.getTags());
        }

        return (StepTask) taskRepository.save(task);
    }

    @Override
    public StepTask updateStepTaskProgress(Integer id, Integer completedSteps, Integer blockedSteps) {
        BaseTask baseTask = getTaskById(id);
        if (!(baseTask instanceof StepTask)) {
            throw new RuntimeException("Task with id: " + id + " is not a StepTask");
        }

        StepTask task = (StepTask) baseTask;
        task.setCompletedSteps(completedSteps);
        task.setBlockedSteps(blockedSteps);

        return (StepTask) taskRepository.save(task);
    }

    @Override
    public StepTask updateStepStatus(Integer id, String stepId, StepTask.StepStatus status) {
        BaseTask baseTask = getTaskById(id);
        if (!(baseTask instanceof StepTask)) {
            throw new RuntimeException("Task with id: " + id + " is not a StepTask");
        }

        StepTask task = (StepTask) baseTask;
        // 这里需要解析stepsJson，更新特定步骤的状态，然后重新序列化
        // 实际实现中应使用JSON库解析和修改steps数据
        // 这里只是一个示例框架

        return (StepTask) taskRepository.save(task);
    }

    // 习惯型任务特有方法
    @Override
    public HabitTask createHabitTask(HabitTask task, Integer goalId) {
        Goal goal = goalRepository.findById(goalId)
                .orElseThrow(() -> new RuntimeException("Goal not found with id: " + goalId));
        task.setGoal(goal);
        task.setType(BaseTask.TaskType.HABIT);
        if (task.getStatus() == null) {
            task.setStatus(BaseTask.Status.ACTIVE);
        }
        return (HabitTask) taskRepository.save(task);
    }

    @Override
    public HabitTask updateHabitTask(Integer id, HabitTask taskDetails) {
        BaseTask baseTask = getTaskById(id);
        if (!(baseTask instanceof HabitTask)) {
            throw new RuntimeException("Task with id: " + id + " is not a HabitTask");
        }

        HabitTask task = (HabitTask) baseTask;
        task.setTitle(taskDetails.getTitle());
        task.setStatus(taskDetails.getStatus());
        task.setCompletionDate(taskDetails.getCompletionDate());
        task.setFrequency(taskDetails.getFrequency());
        task.setDaysOfWeekJson(taskDetails.getDaysOfWeekJson());
        task.setCustomPattern(taskDetails.getCustomPattern());
        task.setCheckinsJson(taskDetails.getCheckinsJson());

        if (taskDetails.getTags() != null && !taskDetails.getTags().isEmpty()) {
            task.setTags(taskDetails.getTags());
        }

        return (HabitTask) taskRepository.save(task);
    }

    @Override
    public HabitTask addCheckin(Integer id, String date, HabitTask.CheckinStatus status, String notes) {
        BaseTask baseTask = getTaskById(id);
        if (!(baseTask instanceof HabitTask)) {
            throw new RuntimeException("Task with id: " + id + " is not a HabitTask");
        }

        HabitTask task = (HabitTask) baseTask;
        // 这里需要调用任务的addCheckin方法，更新打卡记录
        task.addCheckin(date, status, notes);

        return (HabitTask) taskRepository.save(task);
    }

    @Override
    public HabitTask updateStreakInfo(Integer id) {
        BaseTask baseTask = getTaskById(id);
        if (!(baseTask instanceof HabitTask)) {
            throw new RuntimeException("Task with id: " + id + " is not a HabitTask");
        }

        HabitTask task = (HabitTask) baseTask;
        // 计算并更新连续完成信息
        // 这里需要根据checkinsJson计算连续打卡记录

        return (HabitTask) taskRepository.save(task);
    }

    // 创意型任务特有方法
    @Override
    public CreativeTask createCreativeTask(CreativeTask task, Integer goalId) {
        Goal goal = goalRepository.findById(goalId)
                .orElseThrow(() -> new RuntimeException("Goal not found with id: " + goalId));
        task.setGoal(goal);
        task.setType(BaseTask.TaskType.CREATIVE);
        if (task.getStatus() == null) {
            task.setStatus(BaseTask.Status.ACTIVE);
        }
        return (CreativeTask) taskRepository.save(task);
    }

    @Override
    public CreativeTask updateCreativeTask(Integer id, CreativeTask taskDetails) {
        BaseTask baseTask = getTaskById(id);
        if (!(baseTask instanceof CreativeTask)) {
            throw new RuntimeException("Task with id: " + id + " is not a CreativeTask");
        }

        CreativeTask task = (CreativeTask) baseTask;
        task.setTitle(taskDetails.getTitle());
        task.setStatus(taskDetails.getStatus());
        task.setCompletionDate(taskDetails.getCompletionDate());
        task.setVersionsJson(taskDetails.getVersionsJson());
        task.setReviewersJson(taskDetails.getReviewersJson());
        task.setFeedbacksJson(taskDetails.getFeedbacksJson());
        task.setCurrentPhase(taskDetails.getCurrentPhase());
        task.setPublicationFormats(taskDetails.getPublicationFormats());
        task.setLicenseType(taskDetails.getLicenseType());

        if (taskDetails.getTags() != null && !taskDetails.getTags().isEmpty()) {
            task.setTags(taskDetails.getTags());
        }

        return (CreativeTask) taskRepository.save(task);
    }

    @Override
    public CreativeTask updateCreativePhase(Integer id, CreativeTask.CreativePhase phase) {
        BaseTask baseTask = getTaskById(id);
        if (!(baseTask instanceof CreativeTask)) {
            throw new RuntimeException("Task with id: " + id + " is not a CreativeTask");
        }

        CreativeTask task = (CreativeTask) baseTask;
        task.setCurrentPhase(phase);

        return (CreativeTask) taskRepository.save(task);
    }

    @Override
    public CreativeTask addVersion(Integer id, CreativeTask.Version version) {
        BaseTask baseTask = getTaskById(id);
        if (!(baseTask instanceof CreativeTask)) {
            throw new RuntimeException("Task with id: " + id + " is not a CreativeTask");
        }

        CreativeTask task = (CreativeTask) baseTask;

        // 这里需要解析versionsJson，添加新版本，然后重新序列化
        try {
            // 示例实现，实际应用中需要更完善的错误处理
            List<CreativeTask.Version> versions;
            if (task.getVersionsJson() != null && !task.getVersionsJson().isEmpty()) {
                versions = objectMapper.readValue(task.getVersionsJson(),
                        objectMapper.getTypeFactory().constructCollectionType(List.class, CreativeTask.Version.class));
            } else {
                versions = new java.util.ArrayList<>();
            }

            // 设置版本ID和时间戳
            version.setVersionId(java.util.UUID.randomUUID().toString());
            version.setTimestamp(java.time.LocalDateTime.now());
            versions.add(version);

            // 更新versionsJson
            task.setVersionsJson(objectMapper.writeValueAsString(versions));

        } catch (JsonProcessingException e) {
            throw new RuntimeException("Error processing version data", e);
        }

        return (CreativeTask) taskRepository.save(task);
    }

    @Override
    public CreativeTask addFeedback(Integer id, CreativeTask.Feedback feedback) {
        BaseTask baseTask = getTaskById(id);
        if (!(baseTask instanceof CreativeTask)) {
            throw new RuntimeException("Task with id: " + id + " is not a CreativeTask");
        }

        CreativeTask task = (CreativeTask) baseTask;

        // 这里需要解析feedbacksJson，添加新反馈，然后重新序列化
        try {
            // 示例实现，实际应用中需要更完善的错误处理
            List<CreativeTask.Feedback> feedbacks;
            if (task.getFeedbacksJson() != null && !task.getFeedbacksJson().isEmpty()) {
                feedbacks = objectMapper.readValue(task.getFeedbacksJson(),
                        objectMapper.getTypeFactory().constructCollectionType(List.class, CreativeTask.Feedback.class));
            } else {
                feedbacks = new java.util.ArrayList<>();
            }

            feedbacks.add(feedback);

            // 更新feedbacksJson
            task.setFeedbacksJson(objectMapper.writeValueAsString(feedbacks));

        } catch (JsonProcessingException e) {
            throw new RuntimeException("Error processing feedback data", e);
        }

        return (CreativeTask) taskRepository.save(task);
    }

    // 按类型获取任务列表
    @Override
    public List<BaseTask> getTasksByType(BaseTask.TaskType type) {
        return taskRepository.findByType(type);
    }

    @Override
    public List<BaseTask> getTasksByGoalIdAndType(Integer goalId, BaseTask.TaskType type) {
        return taskRepository.findByGoalIdAndType(goalId, type);
    }

    @Override
    public HabitTask performCheckIn(Integer taskId) {
        BaseTask baseTask = getTaskById(taskId); // Uses existing method which throws if not found
        if (!(baseTask instanceof HabitTask)) {
            throw new IllegalArgumentException("Task with id: " + taskId + " is not a HabitTask.");
        }
        HabitTask habitTask = (HabitTask) baseTask;

        LocalDate today = LocalDate.now();
        LocalDate lastCompleted = habitTask.getLastCompleted();

        // Check if already checked in today
        if (lastCompleted != null && lastCompleted.isEqual(today)) {
            log.warn("Habit task {} already checked in today.", taskId);
            // Option 1: Throw exception
            throw new IllegalStateException("Habit task " + taskId + " already checked in today.");
            // Option 2: Silently do nothing and return the task
            // return habitTask;
        }

        // --- Update Check-in Record ---
        Map<String, HabitTask.CheckinRecord> checkins = new HashMap<>();
        String checkinsJson = habitTask.getCheckinsJson();
        if (checkinsJson != null && !checkinsJson.trim().isEmpty()) {
            try {
                // Define the type reference for deserialization
                TypeReference<HashMap<String, HabitTask.CheckinRecord>> typeRef = new TypeReference<HashMap<String, HabitTask.CheckinRecord>>() {
                };
                checkins = objectMapper.readValue(checkinsJson, typeRef);
            } catch (JsonProcessingException e) {
                log.error("Error parsing checkinsJson for task " + taskId + ". JSON: " + checkinsJson, e);
                throw new RuntimeException("Failed to parse check-in data for task " + taskId, e);
            }
        }

        // Create and add today's record
        HabitTask.CheckinRecord todayRecord = new HabitTask.CheckinRecord();
        String todayStr = today.format(DateTimeFormatter.ISO_DATE); // YYYY-MM-DD format
        todayRecord.setDate(todayStr);
        todayRecord.setStatus(HabitTask.CheckinStatus.DONE); // Default to DONE
        // todayRecord.setNotes(""); // Optional notes

        checkins.put(todayStr, todayRecord);

        // Serialize back to JSON
        try {
            habitTask.setCheckinsJson(objectMapper.writeValueAsString(checkins));
        } catch (JsonProcessingException e) {
            log.error("Error serializing checkinsJson for task " + taskId, e);
            throw new RuntimeException("Failed to serialize check-in data for task " + taskId, e);
        }

        // --- Update Streaks (Simple daily logic) ---
        int currentStreak = habitTask.getCurrentStreak() != null ? habitTask.getCurrentStreak() : 0;
        int longestStreak = habitTask.getLongestStreak() != null ? habitTask.getLongestStreak() : 0;

        if (lastCompleted != null && lastCompleted.isEqual(today.minusDays(1))) {
            // Completed yesterday, increment streak
            currentStreak++;
        } else {
            // Didn't complete yesterday (or first check-in), reset streak to 1
            currentStreak = 1;
        }

        habitTask.setCurrentStreak(currentStreak);
        habitTask.setLongestStreak(Math.max(longestStreak, currentStreak));

        // --- Update Last Completed Date ---
        habitTask.setLastCompleted(today);

        // --- Save and Return ---
        log.info("Habit task {} checked in for {}. Current streak: {}", taskId, todayStr, currentStreak);
        return taskRepository.save(habitTask);
    }
}
