package com.xsh.learningtracker.controller;

import java.util.List;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
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
import com.xsh.learningtracker.dto.CategoryDTO;
import com.xsh.learningtracker.dto.GoalDTO;
import com.xsh.learningtracker.dto.SubjectDTO;
import com.xsh.learningtracker.entity.Category;
import com.xsh.learningtracker.entity.Goal;
import com.xsh.learningtracker.entity.Subject;
import com.xsh.learningtracker.entity.Task;
import com.xsh.learningtracker.service.CategoryService;
import com.xsh.learningtracker.service.SubjectService;
import com.xsh.learningtracker.service.UserService;
import com.xsh.learningtracker.service.subjectCategoryService;
import com.xsh.learningtracker.util.DTOConverter;

import jakarta.servlet.http.HttpServletRequest;

@RestController
@RequestMapping("/api/subjects")
public class SubjectController {
    private static final Logger logger = LoggerFactory.getLogger(SubjectController.class);

    @Autowired
    private subjectCategoryService subjectCategoryService;

    @Autowired
    private SubjectService subjectService;

    @Autowired
    private CategoryService categoryService;

    @Autowired
    private UserService userService;

    @GetMapping
    public ResponseEntity<List<SubjectDTO>> getAllSubjects(HttpServletRequest request) {
        logger.info("GET /api/subjects request received");
        logger.info("Authorization header: {}", request.getHeader("Authorization"));
        logger.info("Request URL: {}", request.getRequestURL());
        logger.info("Remote Host: {}", request.getRemoteHost());

        try {
            List<Subject> subjects = subjectService.getAllSubjects();
            logger.info("Retrieved {} subjects from service", subjects.size());

            List<SubjectDTO> subjectDTOs = subjects.stream()
                    .map(DTOConverter::toSubjectDTO)
                    .collect(Collectors.toList());

            logger.info("Returning {} subject DTOs", subjectDTOs.size());
            return ResponseEntity.ok(subjectDTOs);
        } catch (Exception e) {
            logger.error("Error in getAllSubjects: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    // 保持其他方法不变...
    @GetMapping("/my")
    public ResponseEntity<List<SubjectDTO>> getMySubjects(Authentication authentication) {
        String username = authentication.getName();
        Integer userId = userService.findByUsername(username).getId();
        List<Subject> subjects = subjectService.getSubjectsByUserId(userId);
        List<SubjectDTO> subjectDTOs = subjects.stream()
                .map(DTOConverter::toSubjectDTO)
                .collect(Collectors.toList());
        return ResponseEntity.ok(subjectDTOs);
    }

    @PostMapping
    public ResponseEntity<SubjectDTO> createSubject(
            @RequestBody SubjectDTO.CreateSubjectRequest request,
            Authentication authentication) {
        String username = authentication.getName();
        Integer userId = userService.findByUsername(username).getId();
        Subject subject = DTOConverter.toSubject(request, null); // 用户会在service层设置
        Subject createdSubject = subjectService.createSubject(subject, userId);
        subjectCategoryService.createSubjectCategory(subject.getId(),
                request.getCategoryId());
        return ResponseEntity.ok(DTOConverter.toSubjectDTO(createdSubject));
    }

    @PutMapping("/{id}")
    public ResponseEntity<SubjectDTO> updateSubject(
            @PathVariable Integer id,
            @RequestBody SubjectDTO.UpdateSubjectRequest request) {
        Subject subject = new Subject();
        subject.setTitle(request.getTitle());
        Subject updatedSubject = subjectService.updateSubject(id, subject);

        // 如果提供了分类ID，更新学科与分类的关联
        if (request.getCategoryId() != null) {
            // 先删除旧的关联，再创建新的关联
            subjectCategoryService.deleteBySubjectId(id);
            subjectCategoryService.createSubjectCategory(id, request.getCategoryId());
        }

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

    @GetMapping("/category/{subjectId}")
    public ResponseEntity<CategoryDTO> getCategoryBySubject(@PathVariable Integer subjectId) {
        Integer categoryId = subjectCategoryService.getCategoryIdBySubjectId(subjectId);
        Category category = categoryService.getCategoryById(categoryId);
        if (category == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(DTOConverter.toCategoryDTO(category));
    }

}
