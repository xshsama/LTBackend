package com.xsh.learningtracker.service;

import java.util.List;

import com.xsh.learningtracker.entity.BaseTask;
import com.xsh.learningtracker.entity.CreativeTask;
import com.xsh.learningtracker.entity.Goal;
import com.xsh.learningtracker.entity.HabitTask;
import com.xsh.learningtracker.entity.StepTask;

public interface TaskService {
    // 通用方法 - 适用于所有任务类型
    BaseTask createBaseTask(BaseTask task, Integer goalId);

    BaseTask updateBaseTask(Integer id, BaseTask task);

    void deleteTask(Integer id);

    BaseTask getTaskById(Integer id);

    List<BaseTask> getTasksByGoal(Goal goal);

    List<BaseTask> getTasksByGoalId(Integer goalId);

    List<BaseTask> getTasksByGoalIdAndStatus(Integer goalId, BaseTask.Status status);

    List<BaseTask> getTasksByUserIdOrderByCreatedAtDesc(Integer userId);

    List<BaseTask> getTasksByUserIdAndStatus(Integer userId, BaseTask.Status status);

    boolean existsById(Integer id);

    BaseTask updateTaskStatus(Integer id, BaseTask.Status status);

    // 步骤型任务特有方法
    StepTask createStepTask(StepTask task, Integer goalId);

    StepTask updateStepTask(Integer id, StepTask task);

    StepTask updateStepTaskProgress(Integer id, Integer completedSteps, Integer blockedSteps);

    StepTask updateStepStatus(Integer id, String stepId, StepTask.StepStatus status);

    // 习惯型任务特有方法
    HabitTask createHabitTask(HabitTask task, Integer goalId);

    HabitTask updateHabitTask(Integer id, HabitTask task);

    HabitTask addCheckin(Integer id, String date, HabitTask.CheckinStatus status, String notes);

    HabitTask updateStreakInfo(Integer id);

    HabitTask performCheckIn(Integer taskId); // Method for habit check-in

    // 创意型任务特有方法
    CreativeTask createCreativeTask(CreativeTask task, Integer goalId);

    CreativeTask updateCreativeTask(Integer id, CreativeTask task);

    CreativeTask updateCreativePhase(Integer id, CreativeTask.CreativePhase phase);

    CreativeTask addVersion(Integer id, CreativeTask.Version version);

    CreativeTask addFeedback(Integer id, CreativeTask.Feedback feedback);

    // 按类型获取任务列表
    List<BaseTask> getTasksByType(BaseTask.TaskType type);

    List<BaseTask> getTasksByGoalIdAndType(Integer goalId, BaseTask.TaskType type);
}
