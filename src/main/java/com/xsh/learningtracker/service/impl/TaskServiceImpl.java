package com.xsh.learningtracker.service.impl;

import java.time.LocalDate;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.xsh.learningtracker.entity.Goal;
import com.xsh.learningtracker.entity.Task;
import com.xsh.learningtracker.repository.GoalRepository;
import com.xsh.learningtracker.repository.TaskRepository;
import com.xsh.learningtracker.service.TaskService;

@Service
@Transactional
public class TaskServiceImpl implements TaskService {

    @Autowired
    private TaskRepository taskRepository;

    @Autowired
    private GoalRepository goalRepository;

    @Override
    public Task createTask(Task task, Integer goalId) {
        Goal goal = goalRepository.findById(goalId)
                .orElseThrow(() -> new RuntimeException("Goal not found with id: " + goalId));
        task.setGoal(goal);
        if (task.getStatus() == null) {
            task.setStatus(Task.Status.NOT_STARTED);
        }
        return taskRepository.save(task);
    }

    @Override
    public Task updateTask(Integer id, Task taskDetails) {
        Task task = getTaskById(id);
        task.setTitle(taskDetails.getTitle());
        task.setDescription(taskDetails.getDescription());
        task.setStatus(taskDetails.getStatus());
        task.setPriority(taskDetails.getPriority());
        task.setActualTimeMinutes(taskDetails.getActualTimeMinutes());
        task.setCompletionDate(taskDetails.getCompletionDate());
        if (taskDetails.getTags() != null && !taskDetails.getTags().isEmpty()) {
            task.setTags(taskDetails.getTags());
        }
        return taskRepository.save(task);
    }

    @Override
    public void deleteTask(Integer id) {
        Task task = getTaskById(id);
        taskRepository.delete(task);
    }

    @Override
    public Task getTaskById(Integer id) {
        return taskRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Task not found with id: " + id));
    }

    @Override
    public List<Task> getTasksByGoal(Goal goal) {
        return taskRepository.findByGoal(goal);
    }

    @Override
    public List<Task> getTasksByGoalId(Integer goalId) {
        return taskRepository.findByGoalId(goalId);
    }

    @Override
    public List<Task> getTasksByGoalIdAndStatus(Integer goalId, Task.Status status) {
        return taskRepository.findByGoalIdAndStatus(goalId, status);
    }

    @Override
    public List<Task> getTasksByUserIdOrderByCreatedAtDesc(Integer userId) {
        return taskRepository.findByGoalSubjectUserIdOrderByCreatedAtDesc(userId);
    }

    @Override
    public List<Task> getTasksByUserIdAndStatus(Integer userId, Task.Status status) {
        return taskRepository.findByGoalSubjectUserIdAndStatus(userId, status);
    }

    @Override
    public boolean existsById(Integer id) {
        return taskRepository.existsById(id);
    }

    @Override
    public void updateProgress(Integer id, Integer actualTimeMinutes) {
        Task task = getTaskById(id);
        task.setActualTimeMinutes(actualTimeMinutes);
        // 如果实际时间大于0，任务状态改为进行中
        if (actualTimeMinutes > 0 && task.getStatus() == Task.Status.NOT_STARTED) {
            task.setStatus(Task.Status.IN_PROGRESS);
        }
        // 如果实际时间达到或超过预估时间，任务状态改为已完成
    }

    @Override
    public Task updateTaskStatus(Integer id, Task.Status status) {
        Task task = getTaskById(id);
        task.setStatus(status);

        // 如果任务状态变为已完成，设置完成日期
        if (status == Task.Status.COMPLETED) {
            task.setCompletionDate(LocalDate.now());
        } else {
            task.setCompletionDate(null);
        }

        return taskRepository.save(task);
    }

    @Override
    public Task updateActualTime(Integer id, Integer actualTimeMinutes) {
        Task task = getTaskById(id);
        task.setActualTimeMinutes(actualTimeMinutes);

        // 如果实际时间大于0，任务状态改为进行中
        if (actualTimeMinutes > 0 && task.getStatus() == Task.Status.NOT_STARTED) {
            task.setStatus(Task.Status.IN_PROGRESS);
        }

        return taskRepository.save(task);
    }
}
