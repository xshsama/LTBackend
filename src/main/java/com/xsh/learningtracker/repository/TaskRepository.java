package com.xsh.learningtracker.repository;

import java.time.LocalDate;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.xsh.learningtracker.entity.Goal;
import com.xsh.learningtracker.entity.Task;

@Repository
public interface TaskRepository extends JpaRepository<Task, Integer> {
    List<Task> findByGoal(Goal goal);

    List<Task> findByGoalId(Integer goalId);

    List<Task> findByGoalIdAndStatus(Integer goalId, Task.Status status);

    List<Task> findByGoalSubjectUserIdOrderByCreatedAtDesc(Integer userId);

    List<Task> findByGoalSubjectUserIdAndStatus(Integer userId, Task.Status status);

    List<Task> findByGoalSubjectUserIdAndDueDateBetween(
            Integer userId,
            LocalDate startDate,
            LocalDate endDate);

    List<Task> findByGoalSubjectUserIdAndDueDateLessThanEqualAndStatusNot(
            Integer userId,
            LocalDate date,
            Task.Status status);
}
