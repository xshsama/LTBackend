package com.xsh.learningtracker.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.xsh.learningtracker.entity.SubjectCategory;

@Repository
public interface SubjectCategoryRepository extends JpaRepository<SubjectCategory, Integer> {
    // 这里可以添加自定义查询方法
    List<SubjectCategory> findBySubjectId(Integer subjectId);

    List<SubjectCategory> findByCategoryId(Integer categoryId);

    void deleteBySubjectId(Integer subjectId);

    void deleteBySubjectIdAndCategoryId(Integer subjectId, Integer categoryId);
}
