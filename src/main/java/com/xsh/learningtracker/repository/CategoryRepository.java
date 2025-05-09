package com.xsh.learningtracker.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.xsh.learningtracker.entity.Category;

@Repository
public interface CategoryRepository extends JpaRepository<Category, Integer> {

    // 通过subject_category表获取关联的分类
    @Query("SELECT c FROM Category c JOIN SubjectCategory sc ON c.id = sc.categoryId WHERE sc.subjectId = :subjectId")
    List<Category> findBySubjectIdViaSubjectCategory(@Param("subjectId") Integer subjectId);

    // Removed the findBySubjectId method as the direct ManyToOne relationship in
    // Category was incorrect.
    // The query should be done via the intermediate table using
    // findBySubjectIdViaSubjectCategory.
    // List<Category> findBySubjectId(Integer subjectId);
}
