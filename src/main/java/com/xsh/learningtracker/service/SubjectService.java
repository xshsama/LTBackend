package com.xsh.learningtracker.service;

import java.util.List;

import com.xsh.learningtracker.dto.SubjectDTO;
import com.xsh.learningtracker.entity.Subject;
import com.xsh.learningtracker.entity.User;

public interface SubjectService {
    SubjectDTO convertToDTO(Subject subject);

    List<SubjectDTO> getSubjectDTOsByUserId(Integer userId);

    SubjectDTO getSubjectDTOById(Integer id);

    Subject createSubject(Subject subject, Integer userId);

    Subject updateSubject(Integer id, Subject subject);

    void deleteSubject(Integer id);

    Subject getSubjectById(Integer id);

    List<Subject> getSubjectsByUser(User user);

    List<Subject> getSubjectsByUserId(Integer userId);

    boolean existsById(Integer id);

    Subject getSubjectByCategory(Integer categoryId);

    List<Subject> getAllSubjects();
}
