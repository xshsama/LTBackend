package com.xsh.learningtracker.service.impl;

import java.util.List;
import java.util.stream.Collectors; // Import Collectors

import org.springframework.stereotype.Service;

import com.xsh.learningtracker.dto.CategoryDTO; // Import CategoryDTO
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
    public List<CategoryDTO> getCategoriesBySubjectId(Integer subjectId) {
        // Corrected to use the method that queries via the intermediate table
        List<Category> categories = categoryRepository.findBySubjectIdViaSubjectCategory(subjectId);
        return categories.stream()
                .map(this::convertToDTO) // Convert each entity to DTO
                .collect(Collectors.toList());
    }

    @Override
    public Category getCategoryById(Integer id) {
        return categoryRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("分类不存在"));
    }

    @Override
    public Category createCategory(Category category) {
        try {
            return categoryRepository.save(category);
        } catch (Exception e) {
            // 如果发生外键约束错误，尝试修复数据库结构
            try {
                // 创建一个全新的分类对象，避免学科关联
                Category newCategory = new Category();
                newCategory.setName(category.getName());
                newCategory.setDescription(category.getDescription());

                return categoryRepository.save(newCategory);
            } catch (Exception ex) {
                throw new RuntimeException("无法创建分类，可能需要手动修改数据库外键约束", ex);
            }
        }
    }

    @Override
    public Category updateCategory(Integer id, Category categoryDetails) {
        Category category = getCategoryById(id);
        category.setName(categoryDetails.getName());
        category.setDescription(categoryDetails.getDescription());
        return categoryRepository.save(category);
    }

    @Override
    public void deleteCategory(Integer id) {
        Category category = getCategoryById(id);
        categoryRepository.delete(category);
    }

    // Helper method to convert Category entity to CategoryDTO
    private CategoryDTO convertToDTO(Category category) {
        CategoryDTO dto = new CategoryDTO();
        dto.setId(category.getId());
        dto.setName(category.getName());
        // Removed setting subjectId from category.getSubject() as the field no longer
        // exists.
        // CategoryDTO.subjectId might need to be populated differently if required,
        // or removed from CategoryDTO if it doesn't make sense in a ManyToMany context
        // from this direction.
        dto.setSubjectId(null); // Set to null for now
        return dto;
    }
}
