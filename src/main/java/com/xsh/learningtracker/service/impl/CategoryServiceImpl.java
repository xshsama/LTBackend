package com.xsh.learningtracker.service.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.xsh.learningtracker.entity.Category;
import com.xsh.learningtracker.entity.Subject;
import com.xsh.learningtracker.repository.CategoryRepository;
import com.xsh.learningtracker.repository.SubjectRepository;
import com.xsh.learningtracker.service.CategoryService;

@Service
@Transactional
public class CategoryServiceImpl implements CategoryService {

    @Autowired
    private CategoryRepository categoryRepository;

    @Autowired
    private SubjectRepository subjectRepository;

    @Override
    public Category createCategory(Category category, Integer subjectId) {
        Subject subject = subjectRepository.findById(subjectId)
                .orElseThrow(() -> new RuntimeException("Subject not found with id: " + subjectId));
        category.setSubject(subject);
        return categoryRepository.save(category);
    }

    @Override
    public Category updateCategory(Integer id, Category categoryDetails) {
        Category category = getCategoryById(id);
        category.setName(categoryDetails.getName());
        return categoryRepository.save(category);
    }

    @Override
    public void deleteCategory(Integer id) {
        Category category = getCategoryById(id);
        categoryRepository.delete(category);
    }

    @Override
    public Category getCategoryById(Integer id) {
        return categoryRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Category not found with id: " + id));
    }

    @Override
    public List<Category> getCategoriesBySubject(Subject subject) {
        return categoryRepository.findBySubject(subject);
    }

    @Override
    public List<Category> getCategoriesBySubjectId(Integer subjectId) {
        return categoryRepository.findBySubjectId(subjectId);
    }

    @Override
    public List<Category> getCategoriesByUserIdOrderByName(Integer userId) {
        return categoryRepository.findBySubjectUserIdOrderByName(userId);
    }

    @Override
    public boolean existsById(Integer id) {
        return categoryRepository.existsById(id);
    }
}
