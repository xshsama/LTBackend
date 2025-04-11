package com.xsh.learningtracker.service.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.xsh.learningtracker.entity.Subject;
import com.xsh.learningtracker.entity.User;
import com.xsh.learningtracker.exception.UserException;
import com.xsh.learningtracker.repository.SubjectRepository;
import com.xsh.learningtracker.repository.UserRepository;
import com.xsh.learningtracker.service.SubjectService;

@Service
@Transactional
public class SubjectServiceImpl implements SubjectService {

    @Autowired
    private SubjectRepository subjectRepository;

    @Autowired
    private UserRepository userRepository;

    @Override
    public Subject createSubject(Subject subject, Long userId) {
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
    public List<Subject> getSubjectsByUserId(Long userId) {
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
}
