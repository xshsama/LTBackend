package com.xsh.learningtracker.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.xsh.learningtracker.entity.Subject;
import com.xsh.learningtracker.entity.User;

@Repository
public interface SubjectRepository extends JpaRepository<Subject, Integer> {
    List<Subject> findByUser(User user);

    List<Subject> findByUserIdOrderByCreatedAtDesc(Integer userId);

    @Query("SELECT s FROM Subject s JOIN SubjectCategory sc ON s.id = sc.subjectId WHERE sc.categoryId = :categoryId")
    Subject findByCategory(@Param("categoryId") Integer categoryId);
}
