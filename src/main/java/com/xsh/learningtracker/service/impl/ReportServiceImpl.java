package com.xsh.learningtracker.service.impl;

import com.xsh.learningtracker.dto.LearningReportDTO;
import com.xsh.learningtracker.entity.Goal;
import com.xsh.learningtracker.entity.BaseTask;
import com.xsh.learningtracker.entity.Subject; // Needed for SubjectReportStatsDTO
import com.xsh.learningtracker.repository.GoalRepository;
import com.xsh.learningtracker.repository.TaskRepository;
// import com.xsh.learningtracker.repository.UserRepository; // Not directly used for now
import com.xsh.learningtracker.service.AIService;
import com.xsh.learningtracker.service.ReportService;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import lombok.extern.slf4j.Slf4j;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@Slf4j
public class ReportServiceImpl implements ReportService {

    @Autowired
    private GoalRepository goalRepository;

    @Autowired
    private TaskRepository taskRepository;

    // @Autowired
    // private UserRepository userRepository; 

    @Autowired
    private AIService aiService;

    @Autowired
    private ObjectMapper objectMapper;

    @Override
    public LearningReportDTO generateLearningReport(Integer userId, LocalDate startDate, LocalDate endDate) throws Exception {
        log.info("Generating learning report for userId: {}, startDate: {}, endDate: {}", userId, startDate, endDate);

        LearningReportDTO reportDTO = new LearningReportDTO();
        reportDTO.setUserId(userId);
        reportDTO.setGeneratedAt(LocalDateTime.now());
        reportDTO.setReportStartDate(startDate);
        reportDTO.setReportEndDate(endDate);

        // 1. Fetch data
        // These repository methods need to be defined.
        // They should fetch entities whose relevant dates (createdAt, completionDate, etc.) fall within the range.
        List<Goal> allUserGoals = goalRepository.findBySubjectUserIdOrderByCreatedAtDesc(userId);
        List<BaseTask> allUserTasks = taskRepository.findByGoalSubjectUserIdOrderByCreatedAtDesc(userId);

        List<Goal> goalsInPeriod = allUserGoals.stream()
            .filter(g -> (g.getCreatedAt() != null && !g.getCreatedAt().toLocalDate().isAfter(endDate) && !g.getCreatedAt().toLocalDate().isBefore(startDate)) ||
                         (g.getCompletionDate() != null && !g.getCompletionDate().isAfter(endDate) && !g.getCompletionDate().isBefore(startDate)) ||
                         (g.getStatus() == Goal.Status.ONGOING)) // Include ongoing goals regardless of date for some stats
            .collect(Collectors.toList());
        
        List<BaseTask> tasksInPeriod = allUserTasks.stream()
             .filter(t -> (t.getCreatedAt() != null && !t.getCreatedAt().toLocalDate().isAfter(endDate) && !t.getCreatedAt().toLocalDate().isBefore(startDate)) ||
                          (t.getCompletionDate() != null && !t.getCompletionDate().isAfter(endDate) && !t.getCompletionDate().isBefore(startDate)) ||
                          (t.getStatus() == BaseTask.Status.IN_PROGRESS || t.getStatus() == BaseTask.Status.NOT_STARTED)) // Include active tasks
            .collect(Collectors.toList());


        // 2. Calculate Overall Stats
        LearningReportDTO.ReportOverallStatsDTO overallStats = new LearningReportDTO.ReportOverallStatsDTO();
        overallStats.setTotalGoals((int) allUserGoals.stream().filter(g -> g.getCreatedAt().toLocalDate().isBefore(endDate.plusDays(1))).count()); // Goals created before end of period
        overallStats.setCompletedGoals((int) goalsInPeriod.stream().filter(g -> g.getStatus() == Goal.Status.COMPLETED && g.getCompletionDate() != null && !g.getCompletionDate().isAfter(endDate) && !g.getCompletionDate().isBefore(startDate)).count());
        overallStats.setInProgressGoals((int) goalsInPeriod.stream().filter(g -> g.getStatus() == Goal.Status.ONGOING).count());
        overallStats.setTotalTasks((int) allUserTasks.stream().filter(t -> t.getCreatedAt().toLocalDate().isBefore(endDate.plusDays(1))).count()); // Tasks created before end of period
        overallStats.setCompletedTasks((int) tasksInPeriod.stream().filter(t -> t.getStatus() == BaseTask.Status.COMPLETED && t.getCompletionDate() != null && !t.getCompletionDate().isAfter(endDate) && !t.getCompletionDate().isBefore(startDate)).count());
        reportDTO.setOverallStats(overallStats);

        // 3. Calculate Subject Stats
        Map<Subject, List<Goal>> goalsBySubject = goalsInPeriod.stream()
            .filter(g -> g.getSubject() != null)
            .collect(Collectors.groupingBy(Goal::getSubject));

        List<LearningReportDTO.SubjectReportStatsDTO> subjectStatsList = new ArrayList<>();
        for (Map.Entry<Subject, List<Goal>> entry : goalsBySubject.entrySet()) {
            Subject subject = entry.getKey();
            List<Goal> subjectGoals = entry.getValue();
            LearningReportDTO.SubjectReportStatsDTO subjectStat = new LearningReportDTO.SubjectReportStatsDTO();
            subjectStat.setSubjectId(subject.getId());
            subjectStat.setSubjectTitle(subject.getTitle());
            subjectStat.setTotalGoals(subjectGoals.size());
            subjectStat.setCompletedGoals((int) subjectGoals.stream().filter(g -> g.getStatus() == Goal.Status.COMPLETED && g.getCompletionDate() != null && !g.getCompletionDate().isAfter(endDate) && !g.getCompletionDate().isBefore(startDate)).count());
            
            List<BaseTask> tasksForSubject = tasksInPeriod.stream()
                .filter(t -> t.getGoal() != null && t.getGoal().getSubject() != null && t.getGoal().getSubject().getId().equals(subject.getId()))
                .collect(Collectors.toList());
            subjectStat.setTotalTasks(tasksForSubject.size());
            subjectStat.setCompletedTasks((int) tasksForSubject.stream().filter(t -> t.getStatus() == BaseTask.Status.COMPLETED && t.getCompletionDate() != null && !t.getCompletionDate().isAfter(endDate) && !t.getCompletionDate().isBefore(startDate)).count());
            subjectStatsList.add(subjectStat);
        }
        reportDTO.setSubjectStats(subjectStatsList);

        // 4. Populate Recent Activities (e.g., last 5 completed tasks/goals in period)
        List<LearningReportDTO.RecentActivityItemDTO> recentActivities = new ArrayList<>();
        tasksInPeriod.stream()
            .filter(t -> t.getStatus() == BaseTask.Status.COMPLETED && t.getCompletionDate() != null && !t.getCompletionDate().isAfter(endDate) && !t.getCompletionDate().isBefore(startDate))
            .sorted((t1, t2) -> t2.getCompletionDate().compareTo(t1.getCompletionDate()))
            .limit(3)
            .forEach(t -> recentActivities.add(new LearningReportDTO.RecentActivityItemDTO(t.getId(), t.getTitle(), "TASK", "COMPLETED", t.getCompletionDate())));
        
        goalsInPeriod.stream()
            .filter(g -> g.getStatus() == Goal.Status.COMPLETED && g.getCompletionDate() != null && !g.getCompletionDate().isAfter(endDate) && !g.getCompletionDate().isBefore(startDate))
            .sorted((g1, g2) -> g2.getCompletionDate().compareTo(g1.getCompletionDate()))
            .limit(2)
            .forEach(g -> recentActivities.add(new LearningReportDTO.RecentActivityItemDTO(g.getId(), g.getTitle(), "GOAL", "COMPLETED", g.getCompletionDate())));
        reportDTO.setRecentActivities(recentActivities);

        // 5. (Optional) Prepare data for charts (e.g., tasks completed per day/week)
        // This would require more complex grouping and counting. For simplicity, an example:
        Map<LocalDate, Long> tasksCompletedByDate = tasksInPeriod.stream()
            .filter(t -> t.getStatus() == BaseTask.Status.COMPLETED && t.getCompletionDate() != null && !t.getCompletionDate().isAfter(endDate) && !t.getCompletionDate().isBefore(startDate))
            .collect(Collectors.groupingBy(BaseTask::getCompletionDate, Collectors.counting()));
        
        List<LearningReportDTO.ChartDataPointDTO> chartDataPoints = new ArrayList<>();
        tasksCompletedByDate.entrySet().stream()
            .sorted(Map.Entry.comparingByKey())
            .forEach(e -> chartDataPoints.add(new LearningReportDTO.ChartDataPointDTO(e.getKey().toString(), e.getValue())));
        reportDTO.setTasksCompletedOverTime(chartDataPoints);


        // 6. Generate AI Summary
        String learningReportDataAsJson = "";
        try {
            // We only need to pass relevant parts to AI, not necessarily the whole DTO with redundant dates
            // Create a simplified DTO or Map for AI if needed. For now, passing the main DTO.
            learningReportDataAsJson = objectMapper.writeValueAsString(reportDTO); 
        } catch (JsonProcessingException e) {
            log.error("Error serializing LearningReportDTO to JSON for AI prompt: {}", e.getMessage());
            // Decide if to proceed without AI summary or throw error
        }

        if (!learningReportDataAsJson.isEmpty()) {
            // The prompt will be constructed within the AIService or passed here
            // For now, assuming AIService takes the structured data and handles prompt internally
            try {
                 String aiSummaryText = aiService.generateReportSummary(reportDTO); // Pass DTO directly
                 reportDTO.setAiSummary(aiSummaryText);
            } catch (Exception e) {
                log.error("Error generating AI summary: {}", e.getMessage());
                reportDTO.setAiSummary("Failed to generate AI summary at this time."); // Placeholder or error message
            }
        }
        
        return reportDTO;
    }
}