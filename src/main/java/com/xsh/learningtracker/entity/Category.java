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

    // 一个分类可以被多个科目使用，但每个科目只能有一个分类
    @Column(name = "subject_id", insertable = false, updatable = false)
    private Integer subjectId;

    // 这个是为了向后兼容性，但在多对一关系中不应使用
    @ManyToOne(optional = true)
    @JoinColumn(name = "subject_id", nullable = true)
    private Subject subject;

    // 多对一关系：一个分类关联多个学科
    @jakarta.persistence.OneToMany(mappedBy = "category")
    @lombok.Builder.Default
    private Set<Subject> subjects = new java.util.HashSet<>();
}
