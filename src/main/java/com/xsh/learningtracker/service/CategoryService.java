package com.xsh.learningtracker.service;

import java.util.List;

import com.xsh.learningtracker.entity.Category;

public interface CategoryService {

    List<Category> getAllCategories();

    List<Category> getCategoriesBySubjectId(Integer subjectId);

    Category getCategoryById(Integer id);

    Category createCategory(Category category);

    Category updateCategory(Integer id, Category category);

    void deleteCategory(Integer id);
}
