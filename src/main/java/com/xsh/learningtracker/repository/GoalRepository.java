package com.xsh.learningtracker.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.xsh.learningtracker.entity.Goal;
import com.xsh.learningtracker.entity.Subject;

@Repository
public interface GoalRepository extends JpaRepository<Goal, Integer> {
    List<Goal> findBySubject(Subject subject);

    List<Goal> findBySubjectId(Integer subjectId);

    List<Goal> findBySubjectIdAndStatus(Integer subjectId, Goal.Status status);

    List<Goal> findBySubjectUserIdOrderByCreatedAtDesc(Integer userId);

    List<Goal> findBySubjectUserIdAndStatus(Integer userId, Goal.Status status);
}
