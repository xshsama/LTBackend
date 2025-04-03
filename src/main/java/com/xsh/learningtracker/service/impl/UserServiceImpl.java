package com.xsh.learningtracker.service.impl;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.xsh.learningtracker.dto.LoginRequest;
import com.xsh.learningtracker.dto.RegisterRequest;
import com.xsh.learningtracker.dto.UserDTO;
import com.xsh.learningtracker.entity.User;
import com.xsh.learningtracker.repository.UserRepository;
import com.xsh.learningtracker.service.UserService;
import com.xsh.learningtracker.util.JwtUtil;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;
    private final AuthenticationManager authenticationManager;

    @Override
    @Transactional
    public UserDTO register(RegisterRequest request) {
        if (userRepository.existsByUsername(request.getUsername())) {
            throw new BadCredentialsException("用户名已存在");
        }

        User user = new User();
        user.setUsername(request.getUsername());
        user.setPassword(passwordEncoder.encode(request.getPassword()));

        User savedUser = userRepository.save(user);
        return convertToDTO(savedUser);
    }

    @Override
    public String login(LoginRequest request) {
        try {
            // 使用AuthenticationManager进行认证
            // 直接进行认证，不需要保存authentication变量
            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            request.getUsername(),
                            request.getPassword()));

            // 认证成功后生成令牌
            return jwtUtil.generateToken(request.getUsername());
        } catch (Exception e) {
            throw new BadCredentialsException("用户名或密码错误");
        }
    }

    @Override
    public User findByUsername(String username) {
        return userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("用户不存在"));
    }

    private UserDTO convertToDTO(User user) {
        UserDTO dto = new UserDTO();
        dto.setId(user.getId());
        dto.setUsername(user.getUsername());
        dto.setCreatedAt(user.getCreatedAt());
        return dto;
    }
}
