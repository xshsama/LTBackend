package com.xsh.learningtracker.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.xsh.learningtracker.entity.BaseTask;
import com.xsh.learningtracker.entity.Goal;

@Repository
public interface TaskRepository extends JpaRepository<BaseTask, Integer> {
        List<BaseTask> findByGoal(Goal goal);

        List<BaseTask> findByGoalId(Integer goalId);

        /**
         * Counts the number of tasks associated with a specific goal.
         * 
         * @param goalId The ID of the goal.
         * @return The total number of tasks for the given goal.
         */
        long countByGoalId(Integer goalId);

        List<BaseTask> findByGoalIdAndStatus(Integer goalId, BaseTask.Status status);

        List<BaseTask> findByGoalSubjectUserIdOrderByCreatedAtDesc(Integer userId);

        List<BaseTask> findByGoalSubjectUserIdAndStatus(Integer userId, BaseTask.Status status);

        List<BaseTask> findByType(BaseTask.TaskType type);

        List<BaseTask> findByGoalIdAndType(Integer goalId, BaseTask.TaskType type);
}
