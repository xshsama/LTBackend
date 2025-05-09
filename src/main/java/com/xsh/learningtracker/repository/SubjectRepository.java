package com.xsh.learningtracker.repository;

import java.util.List;
import java.util.Optional; // Ensure Optional is imported

import org.springframework.data.jpa.repository.EntityGraph; // Import EntityGraph
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.xsh.learningtracker.entity.Subject;
import com.xsh.learningtracker.entity.User;

@Repository
public interface SubjectRepository extends JpaRepository<Subject, Integer> {
    List<Subject> findByUser(User user);

    // Use @EntityGraph to define fetch plan, let JPA derive query from method name
    @EntityGraph(attributePaths = { "goals", "goals.tasks" }, type = EntityGraph.EntityGraphType.FETCH)
    List<Subject> findByUserIdOrderByCreatedAtDesc(@Param("userId") Integer userId);

    // This query seems to be based on the old SubjectCategory relation, might need
    // review/removal
    @Query("SELECT s FROM Subject s JOIN SubjectCategory sc ON s.id = sc.subjectId WHERE sc.categoryId = :categoryId")
    Subject findByCategory(@Param("categoryId") Integer categoryId);

    // Updated query to fetch necessary associations for getAllTags to work
    // correctly
    @Query("SELECT DISTINCT s FROM Subject s LEFT JOIN FETCH s.goals g LEFT JOIN FETCH g.tasks")
    List<Subject> findAllWithGoalsAndTasks();

    List<Subject> findAll();

    // Use @EntityGraph with a simplified Query
    @EntityGraph(attributePaths = { "goals", "goals.tasks" }, type = EntityGraph.EntityGraphType.FETCH)
    @Query("SELECT s FROM Subject s WHERE s.id = :id")
    Optional<Subject> findByIdWithDetails(@Param("id") Integer id); // Use Optional from java.util
}
