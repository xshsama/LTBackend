package com.xsh.learningtracker.service.impl;

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

}
