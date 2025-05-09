package com.xsh.learningtracker.dto;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import com.xsh.learningtracker.entity.Goal; // For enums

import lombok.Data;

@Data
public class GoalDTO {
    private Integer id;
    private String title;
    private Goal.Status status; // Using the enum from Goal entity
    private LocalDate completionDate;
    private Goal.Priority priority; // Using the enum from Goal entity
    private Integer progress;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private CategoryDTO category;
    private Integer subjectId; // Added Subject ID
    private String subjectTitle; // Added Subject Title
    private List<String> tags; // Combined tags from Goal and its Tasks
    private Integer totalTasks;
    private Integer completedTasks;
    // Optionally, could include List<TaskDTO> tasks if needed in the future

    @Data
    public static class CreateGoalRequest {
        private String title;
        private Goal.Priority priority;
        private Integer subjectId;
        private Integer categoryId; // Optional: for associating with a category upon creation
        private LocalDate completionDate; // Optional: target completion date
    }

    @Data
    public static class UpdateGoalRequest {
        private String title;
        private Goal.Status status;
        private LocalDate completionDate;
        private Goal.Priority priority;
        private Integer progress;
        private Integer categoryId; // To update category association
    }

    @Data
    public static class UpdateGoalProgressRequest {
        private Integer progress;
    }

    @Data
    public static class UpdateStatusRequest {
        private Goal.Status status;
    }
}
