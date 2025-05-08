package com.xsh.learningtracker.util;

import java.util.ArrayList;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.xsh.learningtracker.dto.CategoryDTO;
import com.xsh.learningtracker.dto.CreativeTaskDetailDTO;
import com.xsh.learningtracker.dto.GoalDTO;
import com.xsh.learningtracker.dto.GoalDTO.UpdateGoalRequest;
import com.xsh.learningtracker.dto.HabitTaskDetailDTO;
import com.xsh.learningtracker.dto.StepTaskDetailDTO;
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

        // 根据任务类型添加特定字段
        if (task instanceof StepTask) {
            StepTask stepTask = (StepTask) task;
            StepTaskDetailDTO detail = new StepTaskDetailDTO();
            detail.setCompletedSteps(stepTask.getCompletedSteps());
            detail.setBlockedSteps(stepTask.getBlockedSteps());
            dto.setStepTaskDetail(detail);
        } else if (task instanceof HabitTask) {
            HabitTask habitTask = (HabitTask) task;
            HabitTaskDetailDTO detail = new HabitTaskDetailDTO();
            detail.setFrequency(habitTask.getFrequency());
            detail.setDaysOfWeek(habitTask.getDaysOfWeekJson());
            detail.setCurrentStreak(habitTask.getCurrentStreak());
            detail.setLongestStreak(habitTask.getLongestStreak());
            detail.setLastCompleted(habitTask.getLastCompleted());
            dto.setHabitTaskDetail(detail);
        } else if (task instanceof CreativeTask) {
            CreativeTask creativeTask = (CreativeTask) task;
            CreativeTaskDetailDTO detail = new CreativeTaskDetailDTO();
            detail.setCurrentPhase(creativeTask.getCurrentPhase().toString());
            detail.setPublicationFormats(creativeTask.getPublicationFormats());
            detail.setLicenseType(creativeTask.getLicenseType());
            detail.setValidationScore(creativeTask.getValidationScore());
            dto.setCreativeTaskDetail(detail);
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
                // 设置默认的创作阶段，避免null值导致discriminator错误
                ((CreativeTask) task).setCurrentPhase(CreativeTask.CreativePhase.DRAFTING);
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

        // 确保设置默认状态，防止discriminator值为空
        task.setStatus(BaseTask.Status.ACTIVE);

        return task;
    }

    public static BaseTask toBaseTask(TaskDTO.UpdateTaskRequest request, BaseTask existingTask) {
        // 只有当请求中明确提供了title时才更新
        if (request.getTitle() != null) {
            existingTask.setTitle(request.getTitle());
        }

        // 只有当请求中明确提供了status时才更新
        if (request.getStatus() != null) {
            existingTask.setStatus(request.getStatus());
        }
        // 注意：如果业务逻辑要求在request.status为null时设置默认值，则需要额外处理，
        // 但通常更新操作不应随意更改未指定更新的字段的状态。
        // BaseTask实体本身有status的默认值和nullable=false约束，所以existingTask.status不应为null。

        // 只有当请求中明确提供了completionDate时才更新 (允许设为null以清除日期)
        if (request.getCompletionDate() != null || request.getStatus() == BaseTask.Status.ARCHIVED
                || request.getStatus() == BaseTask.Status.COMPLETED) {
            // 如果状态是完成/归档，即使没传日期，也可能需要设置。或者，如果传了日期就用传的。
            // 这里简化为：如果请求中提供了completionDate，就用它。
            // 如果你的业务逻辑是“当状态变为COMPLETED/ARCHIVED时自动设置completionDate”，
            // 那么这个逻辑应该在Service层，而不是DTO转换层。
            // 或者，如果允许通过API将completionDate设为null，则需要更复杂的逻辑。
            // 目前，如果request.getCompletionDate()是null，则不更新。
            if (request.getCompletionDate() != null) {
                existingTask.setCompletionDate(request.getCompletionDate());
            }
        }

        // 根据具体任务类型设置特定字段 (只在提供时更新)
        if (existingTask instanceof StepTask) {
            StepTask stepTask = (StepTask) existingTask;
            if (request.getStepsJson() != null) {
                stepTask.setStepsJson(request.getStepsJson());
            }
            if (request.getCompletedSteps() != null) {
                stepTask.setCompletedSteps(request.getCompletedSteps());
            }
            if (request.getBlockedSteps() != null) {
                stepTask.setBlockedSteps(request.getBlockedSteps());
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
