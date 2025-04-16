package com.xsh.learningtracker.service.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.xsh.learningtracker.entity.SubjectCategory;
import com.xsh.learningtracker.repository.SubjectCategoryRepository;
import com.xsh.learningtracker.service.subjectCategoryService;

@Service
public class subjectCategoryServiceImpl implements subjectCategoryService {

    private final SubjectCategoryRepository subjectCategoryRepository;

    @Autowired
    public subjectCategoryServiceImpl(SubjectCategoryRepository subjectCategoryRepository) {
        this.subjectCategoryRepository = subjectCategoryRepository;
    }

    @Override
    public SubjectCategory createSubjectCategory(Integer subjectId, Integer categoryId) {
        SubjectCategory subjectCategory = new SubjectCategory();
        subjectCategory.setSubjectId(subjectId);
        subjectCategory.setCategoryId(categoryId);
        subjectCategory.setCreatedAt(java.time.LocalDateTime.now());

        return subjectCategoryRepository.save(subjectCategory);
    }

    @Override
    public Integer getCategoryIdBySubjectId(Integer subjectId) {
        List<SubjectCategory> subjectCategories = subjectCategoryRepository.findBySubjectId(subjectId);
        if (subjectCategories == null || subjectCategories.isEmpty()) {
            return null;
        }
        // 假设一个学科只关联一个分类，返回第一个关联的分类ID
        return subjectCategories.get(0).getCategoryId();
    }

    @Override
    public void deleteBySubjectId(Integer subjectId) {
        // 删除指定学科ID的所有关联记录
        List<SubjectCategory> subjectCategories = subjectCategoryRepository.findBySubjectId(subjectId);
        if (subjectCategories != null && !subjectCategories.isEmpty()) {
            subjectCategoryRepository.deleteAll(subjectCategories);
        }
    }

}
