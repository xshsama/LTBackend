package com.xsh.learningtracker.controller;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.xsh.learningtracker.dto.ApiResponse;
import com.xsh.learningtracker.dto.GoalDTO;
import com.xsh.learningtracker.dto.SubjectDTO;
import com.xsh.learningtracker.entity.Goal;
import com.xsh.learningtracker.entity.Subject;
import com.xsh.learningtracker.entity.Task;
import com.xsh.learningtracker.service.CategoryService;
import com.xsh.learningtracker.service.SubjectService;
import com.xsh.learningtracker.service.UserService;
import com.xsh.learningtracker.util.DTOConverter;

@RestController
@RequestMapping("/api/subjects")
public class SubjectController {

    @Autowired
    private SubjectService subjectService;

    @Autowired
    private CategoryService categoryService;

    @Autowired
    private UserService userService;

    @GetMapping
    public ResponseEntity<List<SubjectDTO>> getAllSubjects(Authentication authentication) {
        String username = authentication.getName();
        Integer userId = userService.findByUsername(username).getId();
        List<Subject> subjects = subjectService.getSubjectsByUserId(userId);
        List<SubjectDTO> subjectDTOs = subjects.stream()
                .map(DTOConverter::toSubjectDTO)
                .collect(Collectors.toList());
        return ResponseEntity.ok(subjectDTOs);
    }

    @GetMapping("/{id}")
    public ResponseEntity<SubjectDTO> getSubject(@PathVariable Integer id) {
        Subject subject = subjectService.getSubjectById(id);
        return ResponseEntity.ok(DTOConverter.toSubjectDTO(subject));
    }

    @PostMapping
    public ResponseEntity<SubjectDTO> createSubject(
            @RequestBody SubjectDTO.CreateSubjectRequest request,
            Authentication authentication) {
        String username = authentication.getName();
        Integer userId = userService.findByUsername(username).getId();
        Subject subject = DTOConverter.toSubject(request, null); // 用户会在service层设置
        Subject createdSubject = subjectService.createSubject(subject, userId);
        return ResponseEntity.ok(DTOConverter.toSubjectDTO(createdSubject));
    }

    @PutMapping("/{id}")
    public ResponseEntity<SubjectDTO> updateSubject(
            @PathVariable Integer id,
            @RequestBody SubjectDTO.UpdateSubjectRequest request) {
        Subject subject = new Subject();
        subject.setTitle(request.getTitle());
        Subject updatedSubject = subjectService.updateSubject(id, subject);
        return ResponseEntity.ok(DTOConverter.toSubjectDTO(updatedSubject));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> deleteSubject(@PathVariable Integer id) {
        subjectService.deleteSubject(id);
        return ResponseEntity.ok(ApiResponse.success("Subject deleted successfully", null));
    }

    @GetMapping("/{id}/stats")
    public ResponseEntity<SubjectDTO.SubjectStats> getSubjectStats(@PathVariable Integer id) {
        Subject subject = subjectService.getSubjectById(id);
        SubjectDTO.SubjectStats stats = new SubjectDTO.SubjectStats();

        // 计算目标统计
        stats.setTotalGoals(subject.getGoals().size());
        stats.setCompletedGoals((int) subject.getGoals().stream()
                .filter(goal -> goal.getStatus() == Goal.Status.COMPLETED)
                .count());

        // 计算任务统计
        int totalTasks = 0;
        int completedTasks = 0;
        for (Goal goal : subject.getGoals()) {
            totalTasks += goal.getTasks().size();
            completedTasks += goal.getTasks().stream()
                    .filter(task -> task.getStatus() == Task.Status.COMPLETED)
                    .count();
        }
        stats.setTotalTasks(totalTasks);
        stats.setCompletedTasks(completedTasks);
        stats.calculateCompletionRate();

        return ResponseEntity.ok(stats);
    }

    @GetMapping("/{id}/goals")
    public ResponseEntity<List<GoalDTO>> getSubjectGoals(@PathVariable Integer id) {
        Subject subject = subjectService.getSubjectById(id);
        List<GoalDTO> goalDTOs = subject.getGoals().stream()
                .map(DTOConverter::toGoalDTO)
                .collect(Collectors.toList());
        return ResponseEntity.ok(goalDTOs);
    }

    @GetMapping("/category/{categoryId}")
    public ResponseEntity<SubjectDTO> getSubjectByCategory(@PathVariable Integer categoryId) {
        Subject subject = subjectService.getSubjectByCategory(categoryId);
        return ResponseEntity.ok(DTOConverter.toSubjectDTO(subject));
    }
}
