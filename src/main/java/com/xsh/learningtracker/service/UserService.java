package com.xsh.learningtracker.service;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;

import com.xsh.learningtracker.dto.LoginRequest;
import com.xsh.learningtracker.dto.LoginResponseDTO;
import com.xsh.learningtracker.dto.RegisterRequest;
import com.xsh.learningtracker.dto.UpdatePasswordRequest;
import com.xsh.learningtracker.dto.UpdateProfileRequest;
import com.xsh.learningtracker.dto.UserDTO;
import com.xsh.learningtracker.dto.UserProfileDTO;
import com.xsh.learningtracker.entity.User;

public interface UserService {
    /**
     * 用户注册
     * 
     * @param request 注册请求
     * @return 注册成功的用户信息
     */
    UserDTO register(RegisterRequest request);

    /**
     * 用户登录
     * 
     * @param request 登录请求
     * @return 登录响应，包含JWT token和用户信息
     */
    LoginResponseDTO login(LoginRequest request);

    /**
     * 根据用户名查找用户
     * 
     * @param username 用户名
     * @return 用户实体
     */
    User findByUsername(String username);

    /**
     * 获取当前登录用户的个人信息
     * 
     * @param username 用户名
     * @return 用户个人信息
     */
    UserProfileDTO getUserProfile(String username);

    /**
     * 更新当前登录用户的个人信息
     * 
     * @param username 用户名
     * @param request  个人信息更新请求
     * @return 更新后的用户个人信息
     */
    UserProfileDTO updateUserProfile(String username, UpdateProfileRequest request);

    /**
     * 更新当前登录用户的密码
     * 
     * @param username 用户名
     * @param request  密码更新请求
     * @return 更新成功返回true，否则返回false
     */
    boolean updatePassword(@AuthenticationPrincipal UserDetails userDetails, UpdatePasswordRequest request);
}