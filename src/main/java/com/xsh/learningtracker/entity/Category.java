package com.xsh.learningtracker.entity;

// Removed JsonBackReference as the ManyToOne is removed
// import com.fasterxml.jackson.annotation.JsonBackReference;

import jakarta.persistence.Entity;
// Removed FetchType as ManyToOne is removed
// import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
// Removed JoinColumn and ManyToOne as they are no longer used here
// import jakarta.persistence.JoinColumn;
// import jakarta.persistence.ManyToOne;
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

    // Removed the incorrect ManyToOne relationship to Subject
    // private Subject subject;

    // Note: If a ManyToMany relationship with Subject exists via
    // 'subject_categories' table,
    // it might be defined here or in the Subject entity using @ManyToMany and
    // @JoinTable.
    // Based on the user's description, the query logic will rely on the
    // intermediate table directly.
}
