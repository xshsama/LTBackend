package com.xsh.learningtracker.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.xsh.learningtracker.entity.TaskTag;

@Repository
public interface TaskTagRepository extends JpaRepository<TaskTag, Integer> {

    List<TaskTag> findByTask_Id(Integer taskId);

    List<TaskTag> findByTag_Id(Integer tagId);

    void deleteByTask_Id(Integer taskId);

    void deleteByTask_IdAndTag_Id(Integer taskId, Integer tagId);
}