package com.xsh.learningtracker.service;

import com.xsh.learningtracker.dto.LoginRequest;
import com.xsh.learningtracker.dto.RegisterRequest;
import com.xsh.learningtracker.dto.UserDTO;
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
     * @return JWT token
     */
    String login(LoginRequest request);

    /**
     * 根据用户名查找用户
     * 
     * @param username 用户名
     * @return 用户实体
     */
    User findByUsername(String username);
}