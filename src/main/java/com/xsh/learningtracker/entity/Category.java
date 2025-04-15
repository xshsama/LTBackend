package com.xsh.learningtracker.entity;

import java.util.Set;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "categories")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Category {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    private String name;

    private String description;

    // 保留原始的单一关系支持，以保持向后兼容性
    @Column(name = "subject_id", insertable = false, updatable = false)
    private Integer subjectId;

    @ManyToOne(optional = true)
    @JoinColumn(name = "subject_id", nullable = true)
    private Subject subject;

    // 新增多对多关系
    @jakarta.persistence.ManyToMany(mappedBy = "categories")
    private Set<Subject> subjects = new java.util.HashSet<>();
}
