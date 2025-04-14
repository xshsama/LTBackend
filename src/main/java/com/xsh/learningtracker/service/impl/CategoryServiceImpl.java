package com.xsh.learningtracker.service.impl;

import java.util.List;

import org.springframework.stereotype.Service;

import com.xsh.learningtracker.entity.Category;
import com.xsh.learningtracker.repository.CategoryRepository;
import com.xsh.learningtracker.service.CategoryService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class CategoryServiceImpl implements CategoryService {

    private final CategoryRepository categoryRepository;

    @Override
    public List<Category> getAllCategories() {
        return categoryRepository.findAll();
    }

    @Override
    public List<Category> getCategoriesBySubjectId(Integer subjectId) {
        return categoryRepository.findBySubjectId(subjectId);
    }

    @Override
    public Category getCategoryById(Integer id) {
        return categoryRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("分类不存在"));
    }

    @Override
    public Category createCategory(Category category) {
        return categoryRepository.save(category);
    }

    @Override
    public Category updateCategory(Integer id, Category categoryDetails) {
        Category category = getCategoryById(id);
        category.setName(categoryDetails.getName());
        category.setDescription(categoryDetails.getDescription());
        category.setSubjectId(categoryDetails.getSubjectId());
        return categoryRepository.save(category);
    }

    @Override
    public void deleteCategory(Integer id) {
        Category category = getCategoryById(id);
        categoryRepository.delete(category);
    }
}
