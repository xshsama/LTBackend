package com.xsh.learningtracker.service;

import java.util.List;

import com.xsh.learningtracker.entity.Goal;
import com.xsh.learningtracker.entity.Task;

public interface TaskService {
    Task createTask(Task task, Integer goalId);

    Task updateTask(Integer id, Task task);

    void deleteTask(Integer id);

    Task getTaskById(Integer id);

    List<Task> getTasksByGoal(Goal goal);

    List<Task> getTasksByGoalId(Integer goalId);

    List<Task> getTasksByGoalIdAndStatus(Integer goalId, Task.Status status);

    List<Task> getTasksByUserIdOrderByCreatedAtDesc(Integer userId);

    List<Task> getTasksByUserIdAndStatus(Integer userId, Task.Status status);

    boolean existsById(Integer id);

    void updateProgress(Integer id, Integer studyHours);

    Task updateTaskStatus(Integer id, Task.Status status);

    Task updateStudyHours(Integer id, Integer studyHours);
}
