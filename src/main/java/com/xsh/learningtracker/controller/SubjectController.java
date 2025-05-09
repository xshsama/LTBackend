package com.xsh.learningtracker.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.xsh.learningtracker.dto.ApiResponse;
import com.xsh.learningtracker.dto.CategoryDTO; // Import CategoryDTO
import com.xsh.learningtracker.dto.SubjectDTO;
import com.xsh.learningtracker.entity.Subject;
import com.xsh.learningtracker.entity.User;
import com.xsh.learningtracker.service.CategoryService; // Import CategoryService
import com.xsh.learningtracker.service.UserService;
import com.xsh.learningtracker.service.impl.SubjectServiceImpl;

@RestController
@RequestMapping("/api/subjects")
public class SubjectController {

    @Autowired
    private SubjectServiceImpl subjectService;

    @Autowired
    private UserService userService;

    @Autowired
    private CategoryService categoryService; // Inject CategoryService

    /**
     * 获取当前用户的所有学科
     */
    @GetMapping
    public ResponseEntity<ApiResponse<List<SubjectDTO>>> getUserSubjects(
            @AuthenticationPrincipal UserDetails userDetails) {

        // 检查用户是否已登录
        if (userDetails == null) {
            return ResponseEntity.ok(ApiResponse.error(401, "用户未登录"));
        }

        User user = userService.findByUsername(userDetails.getUsername());
        List<SubjectDTO> subjects = subjectService.getSubjectDTOsByUserId(user.getId());

        return ResponseEntity.ok(ApiResponse.success("获取学科列表成功", subjects));
    }

    /**
     * 根据ID获取学科
     */
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<SubjectDTO>> getSubject(
            @PathVariable Integer id,
            @AuthenticationPrincipal UserDetails userDetails) {

        if (userDetails == null) {
            return ResponseEntity.ok(ApiResponse.error(401, "用户未登录"));
        }

        SubjectDTO subject = subjectService.getSubjectDTOById(id);
        return ResponseEntity.ok(ApiResponse.success("获取学科成功", subject));
    }

    /**
     * 创建学科
     */
    @PostMapping
    public ResponseEntity<ApiResponse<SubjectDTO>> createSubject(
            @RequestBody SubjectDTO.CreateSubjectRequest request,
            @AuthenticationPrincipal UserDetails userDetails) {

        // 检查用户是否已登录
        if (userDetails == null) {
            return ResponseEntity.ok(ApiResponse.error(401, "用户未登录，无法创建学科"));
        }

        User user = userService.findByUsername(userDetails.getUsername());

        Subject subject = new Subject();
        subject.setTitle(request.getTitle());

        // Pass categoryId to the service method
        Subject createdSubject = subjectService.createSubject(subject, user.getId(), request.getCategoryId());
        SubjectDTO subjectDTO = subjectService.convertToDTO(createdSubject);

        return ResponseEntity.ok(ApiResponse.success("创建学科成功", subjectDTO));
    }

    /**
     * 更新学科
     */
    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<SubjectDTO>> updateSubject(
            @PathVariable Integer id,
            @RequestBody SubjectDTO.UpdateSubjectRequest request,
            @AuthenticationPrincipal UserDetails userDetails) {

        if (userDetails == null) {
            return ResponseEntity.ok(ApiResponse.error(401, "用户未登录"));
        }

        User user = userService.findByUsername(userDetails.getUsername());
        Subject subject = subjectService.getSubjectById(id);

        // 验证用户是否有权限修改该学科
        if (!subject.getUser().getId().equals(user.getId())) {
            return ResponseEntity.ok(ApiResponse.error(403, "无权修改该学科"));
        }

        subject.setTitle(request.getTitle());
        // Pass categoryId to the service method
        Subject updatedSubject = subjectService.updateSubject(id, subject, request.getCategoryId());
        SubjectDTO subjectDTO = subjectService.convertToDTO(updatedSubject);

        return ResponseEntity.ok(ApiResponse.success("更新学科成功", subjectDTO));
    }

    /**
     * 删除学科
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> deleteSubject(
            @PathVariable Integer id,
            @AuthenticationPrincipal UserDetails userDetails) {

        if (userDetails == null) {
            return ResponseEntity.ok(ApiResponse.error(401, "用户未登录"));
        }

        User user = userService.findByUsername(userDetails.getUsername());
        Subject subject = subjectService.getSubjectById(id);

        // 验证用户是否有权限删除该学科
        if (!subject.getUser().getId().equals(user.getId())) {
            return ResponseEntity.ok(ApiResponse.error(403, "无权删除该学科"));
        }

        subjectService.deleteSubject(id);
        return ResponseEntity.ok(ApiResponse.success("删除学科成功", null));
    }

    /**
     * 获取指定学科下的所有分类
     */
    @GetMapping("/{subjectId}/categories")
    public ResponseEntity<ApiResponse<List<CategoryDTO>>> getCategoriesBySubject(
            @PathVariable Integer subjectId,
            @AuthenticationPrincipal UserDetails userDetails) {

        // 检查用户是否已登录 (可以添加更细致的权限检查，例如用户是否拥有该学科)
        if (userDetails == null) {
            return ResponseEntity.ok(ApiResponse.error(401, "用户未登录"));
        }

        // TODO: 可以添加逻辑验证 subjectId 是否存在以及用户是否有权访问

        List<CategoryDTO> categories = categoryService.getCategoriesBySubjectId(subjectId);

        return ResponseEntity.ok(ApiResponse.success("获取学科分类成功", categories));
    }
}
