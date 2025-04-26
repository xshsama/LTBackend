package com.xsh.learningtracker.util;

import java.util.ArrayList;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.xsh.learningtracker.dto.CategoryDTO;
import com.xsh.learningtracker.dto.GoalDTO;
import com.xsh.learningtracker.dto.GoalDTO.UpdateGoalRequest;
import com.xsh.learningtracker.dto.SubjectDTO;
import com.xsh.learningtracker.dto.TagDTO;
import com.xsh.learningtracker.dto.TaskDTO;
import com.xsh.learningtracker.entity.BaseTask;
import com.xsh.learningtracker.entity.Category;
import com.xsh.learningtracker.entity.CreativeTask;
import com.xsh.learningtracker.entity.Goal;
import com.xsh.learningtracker.entity.HabitTask;
import com.xsh.learningtracker.entity.StepTask;
import com.xsh.learningtracker.entity.Subject;
import com.xsh.learningtracker.entity.Tag;
import com.xsh.learningtracker.entity.User;

@Component
public class DTOConverter {

    private static ObjectMapper objectMapper;

    @Autowired
    public void setObjectMapper(ObjectMapper objectMapper) {
        DTOConverter.objectMapper = objectMapper;
    }

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
        // 修改不兼容的Status比较
        dto.setCompletedTasks((int) subject.getGoals().stream()
                .flatMap(g -> g.getTasks().stream())
                .filter(t -> {
                    if (t.getStatus() != null) {
                        return t.getStatus().equals(BaseTask.Status.ARCHIVED);
                    }
                    return false;
                })
                .count());

        // 计算完成率
        if (dto.getTotalTasks() > 0) {
            dto.setCompletionRate((dto.getCompletedTasks() * 100.0) / dto.getTotalTasks());
        }

        // 收集所有标签 - 转换为字符串列表
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
        // 修改不兼容的Status比较
        dto.setCompletedTasks((int) goal.getTasks().stream()
                .filter(t -> {
                    if (t.getStatus() != null) {
                        return t.getStatus().equals(BaseTask.Status.ARCHIVED);
                    }
                    return false;
                })
                .count());
        dto.setRemainingTasks(dto.getTotalTasks() - dto.getCompletedTasks());

        // 计算完成率
        if (dto.getTotalTasks() > 0) {
            dto.setCompletionRate((dto.getCompletedTasks() * 100.0) / dto.getTotalTasks());
        }

        // 收集所有标签 - 这里存储的是完整的Tag对象
        dto.setTags(new ArrayList<>(goal.getTags()));

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

        // 设置标签
        dto.setTags(request.getTags());

        return dto;
    }

    // BaseTask 转换方法
    public static TaskDTO toTaskDTO(BaseTask task) {
        if (task == null)
            return null;

        TaskDTO dto = new TaskDTO();
        dto.setId(task.getId());
        dto.setTitle(task.getTitle());
        dto.setStatus(task.getStatus());
        dto.setType(task.getType());
        dto.setCompletionDate(task.getCompletionDate());
        dto.setCreatedAt(task.getCreatedAt());
        dto.setUpdatedAt(task.getUpdatedAt());
        dto.setGoalId(task.getGoal().getId());
        dto.setMetadata(task.getMetadata());

        // 根据任务类型添加特定字段
        if (task instanceof StepTask) {
            StepTask stepTask = (StepTask) task;
            dto.setCompletedSteps(stepTask.getCompletedSteps());
            dto.setBlockedSteps(stepTask.getBlockedSteps());
            dto.setValidationScore(stepTask.getValidationScore());
        } else if (task instanceof HabitTask) {
            HabitTask habitTask = (HabitTask) task;
            dto.setFrequency(habitTask.getFrequency());
            dto.setDaysOfWeek(habitTask.getDaysOfWeekJson());
            dto.setCurrentStreak(habitTask.getCurrentStreak());
            dto.setLongestStreak(habitTask.getLongestStreak());
            dto.setLastCompleted(habitTask.getLastCompleted());
        } else if (task instanceof CreativeTask) {
            CreativeTask creativeTask = (CreativeTask) task;
            dto.setCurrentPhase(creativeTask.getCurrentPhase().toString());
            dto.setPublicationFormats(creativeTask.getPublicationFormats());
            dto.setLicenseType(creativeTask.getLicenseType());
        }

        // 收集任务的所有标签
        dto.setTags(task.getTags().stream()
                .map(tag -> {
                    TagDTO tagDto = new TagDTO();
                    tagDto.setId(tag.getId());
                    tagDto.setName(tag.getName());
                    tagDto.setColor(tag.getColor());
                    return tagDto;
                })
                .collect(Collectors.toList()));

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

    // Entity 转换助手方法 - 基础任务
    public static BaseTask toBaseTask(TaskDTO.CreateTaskRequest request, Goal goal) {
        BaseTask task;

        switch (request.getType()) {
            case STEP:
                task = new StepTask();
                if (request.getStepsJson() != null) {
                    ((StepTask) task).setStepsJson(request.getStepsJson());
                }
                break;
            case HABIT:
                task = new HabitTask();
                if (request.getFrequency() != null) {
                    ((HabitTask) task).setFrequency(request.getFrequency());
                }
                if (request.getDaysOfWeekJson() != null) {
                    ((HabitTask) task).setDaysOfWeekJson(request.getDaysOfWeekJson());
                }
                if (request.getCustomPattern() != null) {
                    ((HabitTask) task).setCustomPattern(request.getCustomPattern());
                }
                break;
            case CREATIVE:
                task = new CreativeTask();
                if (request.getPublicationFormats() != null) {
                    ((CreativeTask) task).setPublicationFormats(request.getPublicationFormats());
                }
                if (request.getLicenseType() != null) {
                    ((CreativeTask) task).setLicenseType(request.getLicenseType());
                }
                break;
            default:
                task = new BaseTask();
                break;
        }

        task.setTitle(request.getTitle());
        task.setType(request.getType());
        task.setGoal(goal);
        task.setStatus(BaseTask.Status.ACTIVE);
        task.setMetadata(request.getMetadata());

        return task;
    }

    public static BaseTask toBaseTask(TaskDTO.UpdateTaskRequest request, BaseTask existingTask) {
        existingTask.setTitle(request.getTitle());
        existingTask.setStatus(request.getStatus());
        existingTask.setCompletionDate(request.getCompletionDate());
        existingTask.setMetadata(request.getMetadata());

        // 根据具体任务类型设置特定字段
        if (existingTask instanceof StepTask && request.getStepsJson() != null) {
            ((StepTask) existingTask).setStepsJson(request.getStepsJson());
            if (request.getCompletedSteps() != null) {
                ((StepTask) existingTask).setCompletedSteps(request.getCompletedSteps());
            }
            if (request.getBlockedSteps() != null) {
                ((StepTask) existingTask).setBlockedSteps(request.getBlockedSteps());
            }
        } else if (existingTask instanceof HabitTask) {
            HabitTask habitTask = (HabitTask) existingTask;
            if (request.getFrequency() != null) {
                habitTask.setFrequency(request.getFrequency());
            }
            if (request.getDaysOfWeekJson() != null) {
                habitTask.setDaysOfWeekJson(request.getDaysOfWeekJson());
            }
            if (request.getCustomPattern() != null) {
                habitTask.setCustomPattern(request.getCustomPattern());
            }
            if (request.getCheckinsJson() != null) {
                habitTask.setCheckinsJson(request.getCheckinsJson());
            }
        } else if (existingTask instanceof CreativeTask) {
            CreativeTask creativeTask = (CreativeTask) existingTask;
            if (request.getVersionsJson() != null) {
                creativeTask.setVersionsJson(request.getVersionsJson());
            }
            if (request.getReviewersJson() != null) {
                creativeTask.setReviewersJson(request.getReviewersJson());
            }
            if (request.getFeedbacksJson() != null) {
                creativeTask.setFeedbacksJson(request.getFeedbacksJson());
            }
            if (request.getCurrentPhase() != null) {
                try {
                    creativeTask.setCurrentPhase(CreativeTask.CreativePhase.valueOf(request.getCurrentPhase()));
                } catch (IllegalArgumentException e) {
                    // 忽略无效的阶段值
                }
            }
            if (request.getPublicationFormats() != null) {
                creativeTask.setPublicationFormats(request.getPublicationFormats());
            }
            if (request.getLicenseType() != null) {
                creativeTask.setLicenseType(request.getLicenseType());
            }
        }

        return existingTask;
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
}
