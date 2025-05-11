package com.xsh.learningtracker.util;

import java.util.ArrayList;
import java.util.List;
import java.util.Map; // Added import
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.core.type.TypeReference; // Added import
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
        if (goal.getCategory() != null) {
            dto.setCategory(toCategoryDTO(goal.getCategory()));
        } else {
            dto.setCategory(null);
        }
        // Set Subject info
        if (goal.getSubject() != null) {
            dto.setSubjectId(goal.getSubject().getId());
            dto.setSubjectTitle(goal.getSubject().getTitle());
        }
        // 设置统计信息 (totalTasks and completedTasks are already in our GoalDTO definition
        // via SubjectServiceImpl)
        // For consistency, DTOConverter.toGoalDTO should populate them if this method
        // is still used.
        // My GoalDTO definition has totalTasks and completedTasks, so these setters are
        // fine.
        // The calculation logic here for completedTasks is different (ARCHIVED vs
        // COMPLETED)
        // and might need alignment with SubjectServiceImpl or GoalDTO's own
        // calculation.
        // For now, keeping DTOConverter's logic for its own toGoalDTO method.
        if (goal.getTasks() != null) {
            dto.setTotalTasks(goal.getTasks().size());
            dto.setCompletedTasks((int) goal.getTasks().stream()
                    .filter(t -> t.getStatus() != null && t.getStatus() == BaseTask.Status.COMPLETED) // Aligning to
                                                                                                      // COMPLETED
                    .count());
        } else {
            dto.setTotalTasks(0);
            dto.setCompletedTasks(0);
        }
        // dto.setRemainingTasks(dto.getTotalTasks() - dto.getCompletedTasks()); //
        // GoalDTO does not have remainingTasks
        // dto.setCompletionRate(...); // GoalDTO does not have completionRate

        // 收集所有标签 - GoalDTO expects List<String> (tag names)
        if (goal.getTags() != null) { // These are direct tags on Goal entity
            dto.setTags(goal.getTags().stream().map(Tag::getName).collect(Collectors.toList()));
        } else {
            dto.setTags(new ArrayList<>());
        }

        return dto;
    }

    // This method converts an UpdateGoalRequest to a GoalDTO, which is unusual.
    // Usually, a request DTO is converted to an entity.
    // Assuming it's for some specific purpose.
    public static GoalDTO toGoalDTO(UpdateGoalRequest request) {
        if (request == null)
            return null;
        GoalDTO dto = new GoalDTO();
        // Only set fields present in UpdateGoalRequest AND GoalDTO
        dto.setTitle(request.getTitle());
        dto.setStatus(request.getStatus());
        dto.setPriority(request.getPriority());
        // dto.setCategoryId(request.getCategoryId()); // GoalDTO has 'CategoryDTO
        // category', not 'Integer categoryId'
        // UpdateGoalRequest has 'Integer categoryId'.
        // This would require fetching Category and converting to CategoryDTO.
        // Commenting out to fix compilation, service layer should handle this.

        // dto.setTags(request.getTags()); // UpdateGoalRequest (my definition) does not
        // have getTags()
        // If it were to have tags (e.g. List<String>), GoalDTO.setTags would expect
        // List<String>
        // Commenting out to fix compilation.
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
        dto.setWeight(task.getWeight());
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

            // Convert stepsJson to List<StepTaskDetailDTO.StepDTO>
            if (stepTask.getStepsJson() != null && !stepTask.getStepsJson().isEmpty()) {
                try {
                    // First, deserialize stepsJson into List<StepTask.Step> (the entity's inner
                    // class)
                    TypeReference<List<StepTask.Step>> entityStepsTypeRef = new TypeReference<List<StepTask.Step>>() {
                    };
                    List<StepTask.Step> entitySteps = objectMapper.readValue(stepTask.getStepsJson(),
                            entityStepsTypeRef);

                    // Then, convert List<StepTask.Step> to List<StepTaskDetailDTO.StepDTO>
                    List<StepTaskDetailDTO.StepDTO> dtoSteps = entitySteps.stream()
                            .map(entityStep -> {
                                StepTaskDetailDTO.StepDTO dtoStep = new StepTaskDetailDTO.StepDTO();
                                dtoStep.setId(entityStep.getId());
                                dtoStep.setTitle(entityStep.getTitle());
                                dtoStep.setDescription(entityStep.getDescription());
                                dtoStep.setStatus(entityStep.getStatus());
                                if (entityStep.getOrder() != null) {
                                    dtoStep.setOrder(entityStep.getOrder().intValue());
                                }
                                // validationScore is part of StepTaskDetailDTO.StepDTO but not StepTask.Step
                                // It should be set if available from another source or handled accordingly.
                                // dtoStep.setValidationScore(...);
                                return dtoStep;
                            })
                            .collect(Collectors.toList());
                    detail.setSteps(dtoSteps);
                } catch (Exception e) {
                    System.err.println(
                            "Error parsing stepsJson for StepTask ID " + stepTask.getId() + ": " + e.getMessage());
                    detail.setSteps(new ArrayList<>());
                }
            } else {
                detail.setSteps(new ArrayList<>());
            }
            dto.setStepTaskDetail(detail);
        } else if (task instanceof HabitTask) {
            HabitTask habitTask = (HabitTask) task;
            HabitTaskDetailDTO detail = new HabitTaskDetailDTO();
            detail.setFrequency(habitTask.getFrequency());
            detail.setDaysOfWeek(habitTask.getDaysOfWeekJson());
            detail.setCurrentStreak(habitTask.getCurrentStreak());
            detail.setLongestStreak(habitTask.getLongestStreak());
            detail.setLastCompleted(habitTask.getLastCompleted());

            // Convert checkinsJson to List<CheckInRecordDTO>
            if (habitTask.getCheckinsJson() != null && !habitTask.getCheckinsJson().isEmpty()) {
                try {
                    // Deserialize the JSON string into a Map<String, HabitTask.CheckinRecord>
                    TypeReference<Map<String, HabitTask.CheckinRecord>> typeRef = new TypeReference<Map<String, HabitTask.CheckinRecord>>() {
                    };
                    Map<String, HabitTask.CheckinRecord> checkinRecordsMap = objectMapper
                            .readValue(habitTask.getCheckinsJson(), typeRef);

                    // Convert the Map values (CheckinRecord) to List<CheckInRecordDTO>
                    if (checkinRecordsMap != null) {
                        List<HabitTaskDetailDTO.CheckInRecordDTO> recordDTOs = checkinRecordsMap.values().stream()
                                .map(DTOConverter::toCheckInRecordDTO)
                                .collect(Collectors.toList());
                        detail.setCheckInRecords(recordDTOs);
                    } else {
                        detail.setCheckInRecords(new ArrayList<>());
                    }
                } catch (Exception e) {
                    // Log error or handle
                    System.err.println(
                            "Error parsing checkinsJson for task ID " + habitTask.getId() + ": " + e.getMessage());
                    detail.setCheckInRecords(new ArrayList<>());
                }
            } else {
                detail.setCheckInRecords(new ArrayList<>());
            }

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
                    Object pubFormatsObj = request.getPublicationFormats();
                    if (pubFormatsObj instanceof String) {
                        ((CreativeTask) task).setPublicationFormats((String) pubFormatsObj);
                    } else if (pubFormatsObj instanceof List) {
                        @SuppressWarnings("unchecked")
                        List<String> formatsList = (List<String>) pubFormatsObj;
                        ((CreativeTask) task).setPublicationFormats(String.join(",", formatsList));
                    }
                    // else, do nothing or log a warning if it's an unexpected type
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
        if (request.getWeight() != null) {
            task.setWeight(request.getWeight());
        } // else it will use the default weight from BaseTask
        task.setGoal(goal);

        // 确保设置默认状态，防止discriminator值为空
        task.setStatus(BaseTask.Status.IN_PROGRESS);

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

        if (request.getWeight() != null) {
            existingTask.setWeight(request.getWeight());
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
        goal.setStatus(Goal.Status.ONGOING);
        goal.setProgress(0);
        return goal;
    }

    // Helper method to convert HabitTask.CheckinRecord to
    // HabitTaskDetailDTO.CheckInRecordDTO
    private static HabitTaskDetailDTO.CheckInRecordDTO toCheckInRecordDTO(HabitTask.CheckinRecord record) {
        if (record == null)
            return null;
        HabitTaskDetailDTO.CheckInRecordDTO dto = new HabitTaskDetailDTO.CheckInRecordDTO();
        try {
            if (record.getDate() != null) { // Ensure date string is not null
                dto.setDate(java.time.LocalDate.parse(record.getDate())); // Assuming date is in ISO_DATE format
            }
        } catch (java.time.format.DateTimeParseException e) {
            System.err.println("Error parsing date for check-in record: " + record.getDate() + " - " + e.getMessage());
            // Handle error, e.g., set date to null or a default, or rethrow
            dto.setDate(null);
        }
        dto.setStatus(record.getStatus());
        dto.setNotes(record.getNotes());
        return dto;
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
        goal.setCompletionDate(request.getCompletionDate()); // Added
        goal.setProgress(request.getProgress()); // Added
        // categoryId from request would typically be handled in the service layer
        // to fetch and set the Category entity on the Goal.
        return goal;
    }
}
