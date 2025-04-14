package com.xsh.learningtracker.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.xsh.learningtracker.entity.Tag;

@Repository
public interface TagRepository extends JpaRepository<Tag, Integer> {

    List<Tag> findByUserId(Integer userId);

}
