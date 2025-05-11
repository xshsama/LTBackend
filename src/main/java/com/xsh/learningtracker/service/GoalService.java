package com.xsh.learningtracker.service;

import java.util.List;

import com.xsh.learningtracker.entity.Goal;
import com.xsh.learningtracker.entity.Subject;

public interface GoalService {
    Goal createGoal(Goal goal, Integer subjectId);

    Goal updateGoal(Integer id, Goal goal);

    void deleteGoal(Integer id);

    Goal getGoalById(Integer id);

    List<Goal> getGoalsBySubject(Subject subject); // This might be unused or for a different purpose

    // Renamed and updated to include userId for proper authorization
    List<Goal> findBySubjectIdAndUserId(Integer subjectId, Integer userId);

    List<Goal> getGoalsBySubjectIdAndStatus(Integer subjectId, Goal.Status status);

    List<Goal> getGoalsByUserIdOrderByCreatedAtDesc(Integer userId);

    List<Goal> getGoalsByUserIdAndStatus(Integer userId, Goal.Status status);

    boolean existsById(Integer id);

    void updateProgress(Integer id, Integer progress);
}
