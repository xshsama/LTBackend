package com.xsh.learningtracker.service.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.xsh.learningtracker.dto.SubjectDTO;
import com.xsh.learningtracker.dto.TaskDTO;
import com.xsh.learningtracker.entity.Subject;
import com.xsh.learningtracker.entity.User;
import com.xsh.learningtracker.exception.UserException;
import com.xsh.learningtracker.repository.SubjectRepository;
import com.xsh.learningtracker.repository.UserRepository;
import com.xsh.learningtracker.service.CategoryService;
import com.xsh.learningtracker.service.SubjectService;
import com.xsh.learningtracker.service.TaskService;
import com.xsh.learningtracker.service.subjectCategoryService;

@Service
@Transactional
public class SubjectServiceImpl implements SubjectService {

    @Autowired
    private SubjectRepository subjectRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private TaskService taskService;

    @Autowired
    private CategoryService categoryService;

    @Autowired
    private subjectCategoryService subjectCategoryService;

    @Override
    public Subject createSubject(Subject subject, Integer userId) {
        if (subject.getTitle() == null || subject.getTitle().trim().isEmpty()) {
            throw new IllegalArgumentException("Subject title cannot be null or empty");
        }

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UserException("User not found with id: " + userId));
        subject.setUser(user);
        return subjectRepository.save(subject);
    }

    @Override
    public Subject updateSubject(Integer id, Subject subjectDetails) {
        Subject subject = getSubjectById(id);
        subject.setTitle(subjectDetails.getTitle());
        return subjectRepository.save(subject);
    }

    @Override
    public void deleteSubject(Integer id) {
        Subject subject = getSubjectById(id);
        subjectRepository.delete(subject);
    }

    @Override
    public Subject getSubjectById(Integer id) {
        return subjectRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Subject not found with id: " + id));
    }

    @Override
    public List<Subject> getSubjectsByUser(User user) {
        return subjectRepository.findByUser(user);
    }

    @Override
    public List<Subject> getSubjectsByUserId(Integer userId) {
        return subjectRepository.findByUserIdOrderByCreatedAtDesc(userId);
    }

    @Override
    public boolean existsById(Integer id) {
        return subjectRepository.existsById(id);
    }

    @Override
    public Subject getSubjectByCategory(Integer categoryId) {
        return subjectRepository.findByCategory(categoryId);
    }

    @Override
    public List<Subject> getAllSubjects() {
        return subjectRepository.findAllWithGoalsAndTasks();
    }

    @Override
    public SubjectDTO getSubjectDTOById(Integer id) {
        Subject subject = getSubjectById(id);
        return convertToDTO(subject);
    }

    @Override
    public List<SubjectDTO> getSubjectDTOsByUserId(Integer userId) {
        List<Subject> subjects = getSubjectsByUserId(userId);
        return subjects.stream()
                .<SubjectDTO>map(this::convertToDTO)
                .toList();
    }

    @Override
    public SubjectDTO convertToDTO(Subject subject) {
        SubjectDTO subjectDTO = new SubjectDTO();
        subjectDTO.setId(subject.getId());
        subjectDTO.setTitle(subject.getTitle());
        subjectDTO.setCreatedAt(subject.getCreatedAt());
        subjectDTO.setUpdatedAt(subject.getUpdatedAt());
        // You need to inject GoalService and use its convertToDTO method instead
        // or implement a method to convert Goal objects here
        subjectDTO.setGoals(subject.getGoals().stream()
                .map(goal -> {
                    // Create a simple DTO for Goal objects
                    TaskDTO goalDto = new TaskDTO();
                    goalDto.setId(goal.getId());
                    goalDto.setTitle(goal.getTitle());
                    // Set other properties as needed
                    return goalDto;
                })
                .toList());
        return subjectDTO;
    }

}
