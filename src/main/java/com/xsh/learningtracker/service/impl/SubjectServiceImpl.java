package com.xsh.learningtracker.service.impl;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.xsh.learningtracker.dto.CategoryDTO;
import com.xsh.learningtracker.dto.SubjectDTO;
import com.xsh.learningtracker.entity.Goal;
import com.xsh.learningtracker.entity.Subject;
import com.xsh.learningtracker.entity.Task;
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
        // 使用新的预加载方法替代简单的findAll
        return subjectRepository.findAllWithGoalsAndTasks();
    }

    /**
     * 将Subject实体转换为SubjectDTO，并计算统计数据
     */
    public SubjectDTO convertToDTO(Subject subject) {
        SubjectDTO dto = new SubjectDTO();
        dto.setId(subject.getId());
        dto.setTitle(subject.getTitle());
        dto.setCreatedAt(subject.getCreatedAt());
        dto.setUpdatedAt(subject.getUpdatedAt());

        // 计算目标统计数据
        int totalGoals = subject.getGoals().size();
        int completedGoals = 0;
        int totalTasks = 0;
        int completedTasks = 0;

        // 遍历所有目标，计算任务数量
        for (Goal goal : subject.getGoals()) {
            if (goal.getStatus() == Goal.Status.COMPLETED) {
                completedGoals++;
            }

            // 获取每个目标下的任务
            List<Task> tasks = taskService.getTasksByGoalId(goal.getId());
            if (tasks != null) {
                totalTasks += tasks.size();

                // 计算已完成任务数
                for (Task task : tasks) {
                    if (task.getStatus() == Task.Status.COMPLETED) {
                        completedTasks++;
                    }
                }
            }
        }

        // 设置统计数据
        dto.setTotalGoals(totalGoals);
        dto.setCompletedGoals(completedGoals);
        dto.setTotalTasks(totalTasks);
        dto.setCompletedTasks(completedTasks);

        // 计算完成率
        if (totalTasks > 0) {
            dto.setCompletionRate((double) completedTasks / totalTasks * 100);
        } else {
            dto.setCompletionRate(0.0);
        }

        // 获取分类信息
        Integer categoryId = subjectCategoryService.getCategoryIdBySubjectId(subject.getId());
        if (categoryId != null) {
            try {
                CategoryDTO categoryDTO = new CategoryDTO();
                categoryDTO.setId(categoryId);
                categoryDTO.setName(categoryService.getCategoryById(categoryId).getName());
                dto.setCategory(categoryDTO);
            } catch (Exception e) {
                // 如果获取分类信息失败，不设置分类
            }
        }

        // 获取标签
        if (subject.getTags() != null && !subject.getTags().isEmpty()) {
            dto.setTags(subject.getTags().stream()
                    .map(tag -> tag.getName())
                    .collect(Collectors.toList()));
        }

        return dto;
    }

    /**
     * 获取用户的所有学科DTO，包含统计信息
     */
    public List<SubjectDTO> getSubjectDTOsByUserId(Integer userId) {
        List<Subject> subjects = getSubjectsByUserId(userId);
        return subjects.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    /**
     * 获取单个学科的DTO，包含统计信息
     */
    public SubjectDTO getSubjectDTOById(Integer id) {
        Subject subject = getSubjectById(id);
        return convertToDTO(subject);
    }
}
