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
import com.xsh.learningtracker.entity.Goal;
import com.xsh.learningtracker.service.GoalService;
import com.xsh.learningtracker.service.SubjectService;
import com.xsh.learningtracker.service.UserService;
import com.xsh.learningtracker.util.DTOConverter;

@RestController
@RequestMapping("/api/goals")
public class GoalController {

    @Autowired
    private UserService userService;

    @Autowired
    private GoalService goalService;

    @Autowired
    private SubjectService subjectService;

    @GetMapping
    public ResponseEntity<List<GoalDTO>> getAllGoals(Authentication authentication) {
        String username = authentication.getName();
        Integer userId = userService.findByUsername(username).getId();
        List<Goal> goals = goalService.getGoalsByUserIdOrderByCreatedAtDesc(userId);
        List<GoalDTO> goalDTOs = goals.stream()
                .map(goal -> DTOConverter.toGoalDTO(goal))
                .collect(Collectors.toList());
        return ResponseEntity.ok(goalDTOs);
    }

    @GetMapping("/{id}")
    public ResponseEntity<GoalDTO> getGoal(@PathVariable Integer id) {
        Goal goal = goalService.getGoalById(id);
        return ResponseEntity.ok(DTOConverter.toGoalDTO(goal));
    }

    @PostMapping
    public ResponseEntity<GoalDTO> createGoal(@RequestBody GoalDTO.CreateGoalRequest request) {
        Goal goal = new Goal();
        goal.setTitle(request.getTitle());
        goal.setDeadline(request.getDeadline());
        goal.setPriority(request.getPriority());
        goal.setExpectedHours(request.getExpectedHours());
        goal.setSubject(subjectService.getSubjectById(request.getSubjectId()));
        Goal createdGoal = goalService.createGoal(goal, request.getSubjectId());
        return ResponseEntity.ok(DTOConverter.toGoalDTO(createdGoal));
    }

    @PutMapping("/{id}")
    public ResponseEntity<GoalDTO> updateGoal(
            @PathVariable Integer id,
            @RequestBody GoalDTO.UpdateGoalRequest request) {
        Goal goal = DTOConverter.toGoal(request);
        // 更新时不允许修改Subject关联
        Goal updatedGoal = goalService.updateGoal(id, goal);
        return ResponseEntity.ok(DTOConverter.toGoalDTO(updatedGoal));
    }

    @PutMapping("/{id}/progress")
    public ResponseEntity<GoalDTO> updateGoalProgress(
            @PathVariable Integer id,
            @RequestBody GoalDTO.UpdateGoalProgressRequest request) {
        goalService.updateProgress(id, request.getProgress());
        Goal updatedGoal = goalService.getGoalById(id);
        return ResponseEntity.ok(DTOConverter.toGoalDTO(updatedGoal));
    }

    @PutMapping("/{id}/status")
    public ResponseEntity<GoalDTO> updateGoalStatus(
            @PathVariable Integer id,
            @RequestBody GoalDTO.UpdateStatusRequest request) {
        Goal goal = goalService.getGoalById(id);
        goal.setStatus(request.getStatus());
        Goal updatedGoal = goalService.updateGoal(id, goal);
        return ResponseEntity.ok(DTOConverter.toGoalDTO(updatedGoal));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> deleteGoal(@PathVariable Integer id) {
        goalService.deleteGoal(id);
        return ResponseEntity.ok(ApiResponse.success("Goal deleted successfully", null));
    }
}
