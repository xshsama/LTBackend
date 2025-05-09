package com.xsh.learningtracker.service;

import java.util.List;

import com.xsh.learningtracker.dto.CategoryDTO; // Import CategoryDTO
import com.xsh.learningtracker.entity.Category;

public interface CategoryService {

    List<Category> getAllCategories(); // Keep returning entities for now, or change to DTO if needed elsewhere

    List<CategoryDTO> getCategoriesBySubjectId(Integer subjectId); // Change return type to DTO list

    Category getCategoryById(Integer id); // Keep returning entity for now

    Category createCategory(Category category);

    Category updateCategory(Integer id, Category category);

    void deleteCategory(Integer id);
}
