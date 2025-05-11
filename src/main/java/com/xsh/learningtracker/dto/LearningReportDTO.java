package com.xsh.learningtracker.dto;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class LearningReportDTO {
    private Integer userId;
    private LocalDateTime generatedAt;
    private LocalDate reportStartDate;
    private LocalDate reportEndDate;
    private ReportOverallStatsDTO overallStats;
    private List<SubjectReportStatsDTO> subjectStats;
    private List<RecentActivityItemDTO> recentActivities;
    private String aiSummary; // AI生成的文本总结/分析
    // private List<AchievementReportItemDTO> achievements; // Optional
    private List<ChartDataPointDTO> tasksCompletedOverTime; // Optional

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ReportOverallStatsDTO {
        private int totalGoals;
        private int completedGoals;
        private int inProgressGoals;
        private int totalTasks;
        private int completedTasks;
        // private Long totalLearningTimeMinutes;
        // private Double averageTaskCompletionRate;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class SubjectReportStatsDTO {
        private Integer subjectId;
        private String subjectTitle;
        private int totalGoals;
        private int completedGoals;
        private int totalTasks;
        private int completedTasks;
        // private Long learningTimeMinutes;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class RecentActivityItemDTO {
        private Integer id;
        private String title;
        private String type; // "GOAL" or "TASK"
        private String status; // e.g., "COMPLETED", "CREATED"
        private LocalDate date;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ChartDataPointDTO {
        private String label; // e.g., "2023-01", "Week 1"
        private Number value; // Using Number to be flexible (int, double, etc.)
    }

    // Optional:
    // @Data
    // @NoArgsConstructor
    // @AllArgsConstructor
    // public static class AchievementReportItemDTO {
    // private Integer id;
    // private String title;
    // private String description;
    // private LocalDate achievedDate;
    // }
}