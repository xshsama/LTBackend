package com.xsh.learningtracker.service.impl;

import java.util.ArrayList; // Import ArrayList
import java.util.List;
import java.util.Set; // Import Set
import java.util.stream.Collectors; // Import Collectors

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.xsh.learningtracker.dto.CategoryDTO;
import com.xsh.learningtracker.dto.GoalDTO; // Added import for GoalDTO
import com.xsh.learningtracker.dto.SubjectDTO;
import com.xsh.learningtracker.entity.BaseTask;
import com.xsh.learningtracker.entity.Goal; // Import Goal
import com.xsh.learningtracker.entity.Subject;
import com.xsh.learningtracker.entity.SubjectCategory; // Import SubjectCategory entity
import com.xsh.learningtracker.entity.Tag; // Import Tag
import com.xsh.learningtracker.entity.User;
import com.xsh.learningtracker.exception.UserException;
import com.xsh.learningtracker.repository.SubjectCategoryRepository; // Import SubjectCategoryRepository
import com.xsh.learningtracker.repository.SubjectRepository;
import com.xsh.learningtracker.repository.TaskRepository; // Import TaskRepository
import com.xsh.learningtracker.repository.UserRepository;
import com.xsh.learningtracker.service.CategoryService;
import com.xsh.learningtracker.service.SubjectService;

import lombok.extern.slf4j.Slf4j; // Import Slf4j

@Service
@Transactional
@Slf4j // Add Slf4j annotation for logging
public class SubjectServiceImpl implements SubjectService {

    @Autowired
    private SubjectRepository subjectRepository;

    @Autowired
    private UserRepository userRepository;

    // @Autowired
    // private TaskService taskService; // Using TaskRepository instead for count

    @Autowired
    private TaskRepository taskRepository; // Inject TaskRepository

    @Autowired
    private CategoryService categoryService;

    @Autowired
    private SubjectCategoryRepository subjectCategoryRepository; // Inject SubjectCategoryRepository

    @Override
    public Subject createSubject(Subject subject, Integer userId, Integer categoryId) { // Added categoryId
        if (subject.getTitle() == null || subject.getTitle().trim().isEmpty()) {
            throw new IllegalArgumentException("Subject title cannot be null or empty");
        }

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UserException("User not found with id: " + userId));
        subject.setUser(user);
        Subject createdSubject = subjectRepository.save(subject);

        // Associate with category if categoryId is provided
        if (categoryId != null) {
            // Optional: Add check if category exists
            // if (!categoryService.existsById(categoryId)) { throw new
            // RuntimeException("Category not found"); }
            SubjectCategory association = new SubjectCategory();
            association.setSubjectId(createdSubject.getId());
            association.setCategoryId(categoryId);
            // Assuming SubjectCategory has @PrePersist for createdAt
            subjectCategoryRepository.save(association);
            System.out.println("Associated Subject " + createdSubject.getId() + " with Category " + categoryId);
        }

        return createdSubject;
    }

    @Override
    public Subject updateSubject(Integer id, Subject subjectDetails, Integer categoryId) { // Added categoryId
        Subject subject = getSubjectById(id);
        subject.setTitle(subjectDetails.getTitle());
        Subject updatedSubject = subjectRepository.save(subject);

        // Update category association
        // 1. Remove existing associations for this subject
        subjectCategoryRepository.deleteBySubjectId(id);
        System.out.println("Removed old category associations for Subject " + id);

        // 2. Add new association if categoryId is provided
        if (categoryId != null) {
            // Optional: Add check if category exists
            // if (!categoryService.existsById(categoryId)) { throw new
            // RuntimeException("Category not found"); }
            SubjectCategory association = new SubjectCategory();
            association.setSubjectId(updatedSubject.getId());
            association.setCategoryId(categoryId);
            // Assuming SubjectCategory has @PrePersist for createdAt
            subjectCategoryRepository.save(association);
            System.out.println("Associated Subject " + updatedSubject.getId() + " with new Category " + categoryId);
        } else {
            System.out.println("No new category association provided for Subject " + id);
        }

        return updatedSubject;
    }

    @Override
    public void deleteSubject(Integer id) {
        Subject subject = getSubjectById(id);
        subjectRepository.delete(subject);
    }

    @Override
    public Subject getSubjectById(Integer id) {
        return subjectRepository.findByIdWithDetails(id) // Use the new method with JOIN FETCH
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

    // Updated convertToDTO method to include category, counts, and tags
    // Updated convertToDTO method to include category, counts, and tags
    // Updated convertToDTO method to include category, counts, and tags
    @Override
    public SubjectDTO convertToDTO(Subject subject) {
        log.info("--- Converting Subject ID: {} ---", subject.getId()); // Log Entry Point
        SubjectDTO dto = new SubjectDTO();
        dto.setId(subject.getId());
        dto.setTitle(subject.getTitle());
        dto.setCreatedAt(subject.getCreatedAt());
        dto.setUpdatedAt(subject.getUpdatedAt());

        // 1. Get Category
        List<CategoryDTO> categories = categoryService.getCategoriesBySubjectId(subject.getId());
        log.info("Categories found: {}", (categories != null ? categories.size() : "null")); // Log Category Count
        dto.setCategories(categories != null ? categories : new ArrayList<>()); // Set the list of categories
        log.info("Assigned Categories DTO list size: {}", (categories != null ? categories.size() : 0)); // Log assigned
                                                                                                         // list size

        // 2. Get Total Goals
        Set<Goal> goals = subject.getGoals();
        log.info("Goals found: {}", (goals != null ? goals.size() : "null")); // Log Goal Count
        int totalGoals = goals != null ? goals.size() : 0;
        dto.setTotalGoals(totalGoals);
        log.info("Calculated totalGoals: {}", totalGoals); // Log Total Goals

        // 3. Get Total Tasks
        int totalTasks = 0;
        if (goals != null && !goals.isEmpty()) {
            log.info("Calculating total tasks for {} goals...", goals.size()); // Use log.info
            totalTasks = goals.stream()
                    .mapToInt(goal -> {
                        long count = taskRepository.countByGoalId(goal.getId());
                        log.info("  - Goal ID: {}, Task count: {}", goal.getId(), count); // Use log.info
                        return (int) count;
                    })
                    .sum();
        }
        dto.setTotalTasks(totalTasks);
        log.info("Calculated totalTasks: {}", totalTasks); // Use log.info

        // 4. Get Tags (as List<String>)
        log.info("Attempting to get tags via subject.getTags()..."); // Use log.info
        // Add detailed logging for goal tags before calling subject.getTags()
        if (goals != null) {
            log.info("Checking tags for each goal BEFORE calling subject.getTags():"); // Use log.info
            goals.forEach(goal -> {
                try {
                    // Try accessing tags to see if they are loaded
                    Set<Tag> goalTags = goal.getTags();
                    boolean initialized = org.hibernate.Hibernate.isInitialized(goalTags); // Check if initialized
                    log.info("  - Goal ID: {}, Tags Initialized: {}, Tag count: {}", // Use log.info
                            goal.getId(),
                            initialized,
                            (goalTags != null ? goalTags.size() : "null"));
                    if (initialized && goalTags != null && !goalTags.isEmpty()) {
                        log.info("    Tags: {}", goalTags.stream().map(Tag::getName).collect(Collectors.joining(", "))); // Use
                                                                                                                         // log.info
                    }
                    // Optional: Force initialization here if needed for debugging, but JOIN FETCH
                    // should handle it
                    // org.hibernate.Hibernate.initialize(goal.getTags());
                } catch (Exception e) {
                    log.error("  - Error accessing tags for Goal ID: {} - {}: {}", goal.getId(),
                            e.getClass().getSimpleName(), e.getMessage()); // Use log.error
                }
            });
        } else {
            log.warn("Goals collection is null, cannot check individual goal tags."); // Use log.warn
        }

        Set<Tag> tags = subject.getTags(); // Now call the method
        log.info("Result from subject.getTags() - Size: {}", (tags != null ? tags.size() : "null")); // Use log.info

        List<String> tagNames = tags != null ? tags.stream()
                .map(tag -> {
                    // log.info(" - Mapping Tag ID: {}, Name: {}", tag.getId(), tag.getName()); //
                    // Log mapping if needed
                    return tag.getName(); // Use getName() based on Tag entity
                })
                .collect(Collectors.toList())
                : new ArrayList<>();
        dto.setTags(tagNames);
        log.info("Tag names: {}", tagNames); // Use log.info

        // 5. Populate 'goals' field (List<TaskDTO>) - Keeping existing logic, but it's
        // potentially confusing/incorrect
        if (goals != null) {
            dto.setGoals(goals.stream()
                    .map(this::convertGoalToGoalDTO) // Use helper method to convert Goal to GoalDTO
                    .collect(Collectors.toList()));
        } else {
            dto.setGoals(new ArrayList<>());
        }

        // Renumbered to 6. as 'goals' population is now 5.
        // 6. Calculate completed goals and tasks for the Subject
        int completedGoalsCount = 0;
        int completedTasksCount = 0;

        if (goals != null && !goals.isEmpty()) {
            for (Goal goal : goals) {
                if (goal.getStatus() == Goal.Status.COMPLETED) {
                    completedGoalsCount++;
                }
                // Ensure tasks are loaded (should be due to JOIN FETCH)
                if (goal.getTasks() != null && !goal.getTasks().isEmpty()) {
                    for (BaseTask task : goal.getTasks()) {
                        if (task.getStatus() == BaseTask.Status.COMPLETED) {
                            completedTasksCount++;
                        }
                    }
                }
            }
        }
        dto.setCompletedGoals(completedGoalsCount);
        dto.setCompletedTasks(completedTasksCount);
        log.info("Calculated completedGoals: {}", completedGoalsCount);
        log.info("Calculated completedTasks: {}", completedTasksCount);

        // 6. Calculate completion rate
        if (totalTasks > 0) {
            double completionRate = (double) completedTasksCount * 100.0 / totalTasks;
            dto.setCompletionRate(completionRate);
            log.info("Calculated completionRate: {}", completionRate);
        } else {
            dto.setCompletionRate(0.0); // Or null, depending on preference for no tasks
            log.info("Setting completionRate to 0.0 as totalTasks is 0");
        }

        log.info("Final SubjectDTO before return: {}", dto); // Use log.info
        return dto;
    }

    private GoalDTO convertGoalToGoalDTO(Goal goal) {
        if (goal == null) {
            return null;
        }
        GoalDTO goalDto = new GoalDTO();
        goalDto.setId(goal.getId());
        goalDto.setTitle(goal.getTitle());
        goalDto.setStatus(goal.getStatus());
        goalDto.setCompletionDate(goal.getCompletionDate());
        goalDto.setPriority(goal.getPriority());
        goalDto.setProgress(goal.getProgress());
        goalDto.setCreatedAt(goal.getCreatedAt());
        goalDto.setUpdatedAt(goal.getUpdatedAt());

        // Convert Category entity to CategoryDTO
        if (goal.getCategory() != null) {
            CategoryDTO categoryDto = new CategoryDTO();
            categoryDto.setId(goal.getCategory().getId());
            categoryDto.setName(goal.getCategory().getName());
            // Assuming subjectId for category in GoalDTO context might not be directly
            // relevant
            // or would require fetching subject through goal.getSubject().getId()
            // For simplicity, only id and name are set here.
            goalDto.setCategory(categoryDto);
        }

        // Set tags using getAllTags from Goal entity
        Set<Tag> goalAllTags = goal.getAllTags();
        if (goalAllTags != null) {
            goalDto.setTags(goalAllTags.stream().map(Tag::getName).collect(Collectors.toList()));
        } else {
            goalDto.setTags(new ArrayList<>());
        }

        // Calculate total and completed tasks for this specific goal
        Set<BaseTask> tasks = goal.getTasks(); // Assuming tasks are loaded due to JOIN FETCH
        int totalTasksInGoal = 0;
        int completedTasksInGoal = 0;
        if (tasks != null) {
            totalTasksInGoal = tasks.size();
            for (BaseTask task : tasks) {
                if (task.getStatus() == BaseTask.Status.COMPLETED) {
                    completedTasksInGoal++;
                }
            }
        }
        goalDto.setTotalTasks(totalTasksInGoal);
        goalDto.setCompletedTasks(completedTasksInGoal);

        return goalDto;
    }
}
