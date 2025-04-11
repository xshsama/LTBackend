package com.xsh.learningtracker.service;

import java.util.List;

import com.xsh.learningtracker.entity.Category;
import com.xsh.learningtracker.entity.Subject;

public interface CategoryService {
    Category createCategory(Category category, Integer subjectId);

    Category updateCategory(Integer id, Category category);

    void deleteCategory(Integer id);

    Category getCategoryById(Integer id);

    List<Category> getCategoriesBySubject(Subject subject);

    List<Category> getCategoriesBySubjectId(Integer subjectId);

    List<Category> getCategoriesByUserIdOrderByName(Integer userId);

    boolean existsById(Integer id);
}
