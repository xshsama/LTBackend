package com.xsh.learningtracker.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.xsh.learningtracker.entity.UserPreference;

@Repository
public interface UserPreferenceRepository extends JpaRepository<UserPreference, Integer> {

    /**
     * 根据用户名查找用户偏好设置
     * 
     * @param username 用户名
     * @return 用户偏好设置 Optional
     */
    Optional<UserPreference> findByUserUsername(String username);
}
