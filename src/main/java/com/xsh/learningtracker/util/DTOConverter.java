package com.xsh.learningtracker.util;

import java.util.stream.Collectors;

import com.xsh.learningtracker.dto.CategoryDTO;
import com.xsh.learningtracker.dto.GoalDTO;
import com.xsh.learningtracker.dto.GoalDTO.UpdateGoalRequest;
import com.xsh.learningtracker.dto.SubjectDTO;
import com.xsh.learningtracker.dto.TaskDTO;
import com.xsh.learningtracker.entity.Category;
import com.xsh.learningtracker.entity.Goal;
import com.xsh.learningtracker.entity.Subject;
import com.xsh.learningtracker.entity.Tag;
import com.xsh.learningtracker.entity.Task;
import com.xsh.learningtracker.entity.User;

public class DTOConverter {
    // Subject 转换方法
    public static SubjectDTO toSubjectDTO(Subject subject) {
        if (subject == null)
            return null;
        SubjectDTO dto = new SubjectDTO();
        dto.setId(subject.getId());
        dto.setTitle(subject.getTitle());
        dto.setCreatedAt(subject.getCreatedAt());
        dto.setUpdatedAt(subject.getUpdatedAt());

        // 设置统计信息
        dto.setTotalGoals(subject.getGoals().size());
        dto.setCompletedGoals((int) subject.getGoals().stream()
                .filter(g -> g.getStatus() == Goal.Status.COMPLETED)
                .count());
        dto.setTotalTasks((int) subject.getGoals().stream()
                .mapToLong(g -> g.getTasks().size())
                .sum());
        dto.setCompletedTasks((int) subject.getGoals().stream()
                .flatMap(g -> g.getTasks().stream())
                .filter(t -> t.getStatus() == Task.Status.COMPLETED)
                .count());

        // 计算完成率
        if (dto.getTotalTasks() > 0) {
            dto.setCompletionRate((dto.getCompletedTasks() * 100.0) / dto.getTotalTasks());
        }

        // 收集所有标签
        dto.setTags(subject.getTags().stream()
                .map(Tag::getName)
                .collect(Collectors.toList()));

        return dto;
    }

    // Goal 转换方法
    public static GoalDTO toGoalDTO(Goal goal) {
        if (goal == null)
            return null;
        GoalDTO dto = new GoalDTO();
        dto.setId(goal.getId());
        dto.setTitle(goal.getTitle());
        dto.setStatus(goal.getStatus());
        dto.setPriority(goal.getPriority());
        dto.setProgress(goal.getProgress());
        dto.setCompletionDate(goal.getCompletionDate());
        dto.setCreatedAt(goal.getCreatedAt());
        dto.setUpdatedAt(goal.getUpdatedAt());
        dto.setCategoryId(goal.getCategory() != null ? goal.getCategory().getId() : null);
        dto.setSubjectId(goal.getSubject().getId());

        // 设置统计信息
        dto.setTotalTasks(goal.getTasks().size());
        dto.setCompletedTasks((int) goal.getTasks().stream()
                .filter(t -> t.getStatus() == Task.Status.COMPLETED)
                .count());
        dto.setRemainingTasks(dto.getTotalTasks() - dto.getCompletedTasks());

        // 计算完成率
        if (dto.getTotalTasks() > 0) {
            dto.setCompletionRate((dto.getCompletedTasks() * 100.0) / dto.getTotalTasks());
        }

        // 收集所有标签
        dto.setTags(goal.getTags().stream()
                .map(Tag::getName)
                .collect(Collectors.toList()));

        return dto;
    }

    public static GoalDTO toGoalDTO(UpdateGoalRequest request) {
        if (request == null)
            return null;
        GoalDTO dto = new GoalDTO();
        // 只设置UpdateGoalRequest中存在的字段
        dto.setTitle(request.getTitle());
        dto.setStatus(request.getStatus());
        dto.setPriority(request.getPriority());
        dto.setCategoryId(request.getCategoryId());

        // 直接设置标签，因为getTags()返回的是List<String>
        dto.setTags(request.getTags());

        return dto;
    }

    // Task 转换方法
    public static TaskDTO toTaskDTO(Task task) {
        if (task == null)
            return null;
        TaskDTO dto = new TaskDTO();
        dto.setId(task.getId());
        dto.setTitle(task.getTitle());
        dto.setStatus(task.getStatus());
        dto.setPriority(task.getPriority());
        dto.setCompletionDate(task.getCompletionDate());
        dto.setCreatedAt(task.getCreatedAt());
        dto.setUpdatedAt(task.getUpdatedAt());
        dto.setGoalId(task.getGoal().getId());
        dto.setWeight(task.getWeight());

        return dto;
    }

    // Category 转换方法
    public static CategoryDTO toCategoryDTO(Category category) {
        if (category == null)
            return null;
        CategoryDTO dto = new CategoryDTO();
        dto.setId(category.getId());
        dto.setName(category.getName());
        return dto;
    }

    // Entity 转换助手方法
    public static Task toTask(TaskDTO.CreateTaskRequest request, Goal goal) {
        Task task = new Task();
        task.setTitle(request.getTitle());
        task.setPriority(request.getPriority());
        task.setGoal(goal);
        return task;
    }

    public static Goal toGoal(GoalDTO.CreateGoalRequest request, Subject subject, Category category) {
        Goal goal = new Goal();
        goal.setTitle(request.getTitle());
        goal.setPriority(request.getPriority());
        goal.setSubject(subject);
        goal.setCategory(category);
        goal.setStatus(Goal.Status.NOT_STARTED);
        goal.setProgress(0);
        return goal;
    }

    public static Category toCategory(CategoryDTO.CreateCategoryRequest request, Subject subject) {
        Category category = new Category();
        category.setName(request.getName());
        return category;
    }

    public static Subject toSubject(SubjectDTO.CreateSubjectRequest request, User user) {
        Subject subject = new Subject();
        subject.setTitle(request.getTitle());
        subject.setUser(user);
        return subject;
    }

    public static Goal toGoal(GoalDTO.UpdateGoalRequest request) {
        Goal goal = new Goal();
        goal.setTitle(request.getTitle());
        goal.setStatus(request.getStatus());
        goal.setPriority(request.getPriority());
        return goal;
    }

    public static Task toTask(TaskDTO.UpdateTaskRequest request) {
        Task task = new Task();
        task.setTitle(request.getTitle());
        task.setStatus(request.getStatus());
        task.setPriority(request.getPriority());
        return task;
    }
}
