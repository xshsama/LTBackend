package com.xsh.learningtracker.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.xsh.learningtracker.entity.Category;
import com.xsh.learningtracker.entity.Subject;

@Repository
public interface CategoryRepository extends JpaRepository<Category, Integer> {
    List<Category> findBySubject(Subject subject);

    List<Category> findBySubjectId(Integer subjectId);

    List<Category> findBySubjectUserIdOrderByName(Integer userId);
}
