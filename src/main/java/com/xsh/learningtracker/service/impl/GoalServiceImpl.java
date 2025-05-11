package com.xsh.learningtracker.service.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.xsh.learningtracker.entity.Goal;
import com.xsh.learningtracker.entity.Subject;
import com.xsh.learningtracker.repository.GoalRepository;
import com.xsh.learningtracker.repository.SubjectRepository;
import com.xsh.learningtracker.service.GoalService;

@Service
@Transactional
public class GoalServiceImpl implements GoalService {

    @Autowired
    private GoalRepository goalRepository;

    @Autowired
    private SubjectRepository subjectRepository;

    @Override
    public Goal createGoal(Goal goal, Integer subjectId) {
        Subject subject = subjectRepository.findById(subjectId)
                .orElseThrow(() -> new RuntimeException("Subject not found with id: " + subjectId));
        goal.setSubject(subject);
        // The default status is set in the Goal entity itself.
        // If a status is explicitly provided in the 'goal' object, it will be used.
        // Otherwise, the default 'ONGOING' from the entity will be applied upon
        // persistence.
        return goalRepository.save(goal);
    }

    @Override
    public Goal updateGoal(Integer id, Goal goalDetails) {
        Goal goal = getGoalById(id);
        goal.setTitle(goalDetails.getTitle());
        goal.setStatus(goalDetails.getStatus());
        goal.setPriority(goalDetails.getPriority());
        goal.setProgress(goalDetails.getProgress());
        goal.setCompletionDate(goalDetails.getCompletionDate());
        if (goalDetails.getCategory() != null) {
            goal.setCategory(goalDetails.getCategory());
        }
        return goalRepository.save(goal);
    }

    @Override
    public void deleteGoal(Integer id) {
        Goal goal = getGoalById(id);
        goalRepository.delete(goal);
    }

    @Override
    public Goal getGoalById(Integer id) {
        return goalRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Goal not found with id: " + id));
    }

    @Override
    public List<Goal> getGoalsBySubject(Subject subject) {
        return goalRepository.findBySubject(subject);
    }

    @Override
    public List<Goal> findBySubjectIdAndUserId(Integer subjectId, Integer userId) {
        // 调用新的Repository方法，该方法通过Subject的ID和关联的User的ID来查找Goals
        return goalRepository.findBySubject_IdAndSubject_User_IdOrderByCreatedAtDesc(subjectId, userId);
    }

    @Override
    public List<Goal> getGoalsBySubjectIdAndStatus(Integer subjectId, Goal.Status status) {
        return goalRepository.findBySubjectIdAndStatus(subjectId, status);
    }

    @Override
    public List<Goal> getGoalsByUserIdOrderByCreatedAtDesc(Integer userId) {
        return goalRepository.findBySubjectUserIdOrderByCreatedAtDesc(userId);
    }

    @Override
    public List<Goal> getGoalsByUserIdAndStatus(Integer userId, Goal.Status status) {
        return goalRepository.findBySubjectUserIdAndStatus(userId, status);
    }

    @Override
    public boolean existsById(Integer id) {
        return goalRepository.existsById(id);
    }

    @Override
    public void updateProgress(Integer id, Integer progress) {
        Goal goal = getGoalById(id);
        goal.setProgress(progress);
        if (progress >= 100) {
            goal.setStatus(Goal.Status.COMPLETED);
            goal.setCompletionDate(java.time.LocalDate.now());
        }
        goalRepository.save(goal);
    }
}
