package com.xsh.learningtracker.service;

import com.xsh.learningtracker.entity.SubjectCategory;

public interface subjectCategoryService {
    SubjectCategory createSubjectCategory(Integer subjectId, Integer categoryId);
}
