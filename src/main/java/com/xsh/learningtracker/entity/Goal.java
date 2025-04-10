package com.xsh.learningtracker.entity;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Table;
import lombok.Data;

@Data
@Entity
@Table(name = "goals")
public class Goal {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne
    @JoinColumn(name = "subject_id", nullable = false)
    private Subject subject;

    @Column(name = "title", nullable = false)
    private String title;

    @Column(name = "deadline")
    private LocalDate deadline;

    @Column(name = "status")
    private Status status;

    @Column(name = "completion_date")
    private LocalDate completionDate;

    @Enumerated(EnumType.STRING)
    @Column(name = "priority", nullable = false)
    private Priority priority = Priority.MEDIUM;

    @Column(name = "progress")
    private Integer progress = 0;

    @Column(name = "expected_hours")
    private Integer expectedHours;

    @Column(name = "actual_hours")
    private Integer actualHours = 0;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @ManyToOne
    @JoinColumn(name = "category_id")
    private Category category;

    @OneToMany(mappedBy = "goal")
    private Set<Task> tasks = new HashSet<>();

    public Set<Tag> getTags() {
        return tasks.stream()
                .flatMap(task -> task.getTags().stream())
                .collect(java.util.stream.Collectors.toSet());
    }

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }

    public enum Priority {
        LOW, MEDIUM, HIGH, URGENT
    }

    public enum Status {
        ONGOING, COMPLETED, EXPIRED, NO_STARTED
    }
}
