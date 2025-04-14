package com.xsh.learningtracker.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.xsh.learningtracker.entity.Category;

@Repository
public interface CategoryRepository extends JpaRepository<Category, Integer> {

    List<Category> findBySubjectId(Integer subjectId);

}
