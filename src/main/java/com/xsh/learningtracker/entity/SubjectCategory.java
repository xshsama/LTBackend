package com.xsh.learningtracker.entity;

import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 学科与分类之间的关联实体
 */
@Entity
@Table(name = "subject_categories")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class SubjectCategory {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "subject_id", nullable = false)
    private Integer subjectId;

    @Column(name = "category_id", nullable = false)
    private Integer categoryId;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

}
