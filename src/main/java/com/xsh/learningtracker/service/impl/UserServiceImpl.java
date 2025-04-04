package com.xsh.learningtracker.service.impl;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.xsh.learningtracker.dto.LoginRequest;
import com.xsh.learningtracker.dto.LoginResponseDTO;
import com.xsh.learningtracker.dto.RegisterRequest;
import com.xsh.learningtracker.dto.UpdateProfileRequest;
import com.xsh.learningtracker.dto.UserDTO;
import com.xsh.learningtracker.dto.UserProfileDTO;
import com.xsh.learningtracker.entity.User;
import com.xsh.learningtracker.entity.UserInfo;
import com.xsh.learningtracker.repository.UserInfoRepository;
import com.xsh.learningtracker.repository.UserRepository;
import com.xsh.learningtracker.service.UserService;
import com.xsh.learningtracker.util.JwtUtil;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final UserInfoRepository userInfoRepository;
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
    public LoginResponseDTO login(LoginRequest request) {
        try {
            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            request.getUsername(),
                            request.getPassword()));

            String token = jwtUtil.generateToken(request.getUsername());

            // 获取用户的个人信息
            UserProfileDTO userProfile = getUserProfile(request.getUsername());

            // 返回包含令牌和用户信息的响应
            return new LoginResponseDTO(token, userProfile);
        } catch (Exception e) {
            throw new BadCredentialsException("用户名或密码错误");
        }
    }

    @Override
    public User findByUsername(String username) {
        return userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("用户不存在"));
    }

    @Override
    public UserProfileDTO getUserProfile(String username) {
        User user = findByUsername(username);
        UserInfo userInfo = userInfoRepository.findByUser(user)
                .orElse(new UserInfo());

        return convertToProfileDTO(user, userInfo);
    }

    @Override
    @Transactional
    public UserProfileDTO updateUserProfile(String username, UpdateProfileRequest request) {
        User user = findByUsername(username);
        UserInfo userInfo = userInfoRepository.findByUser(user)
                .orElse(new UserInfo());

        if (userInfo.getId() == null) {
            userInfo.setUser(user);
        }

        userInfo.setNickname(request.getNickname());
        userInfo.setAvatar(request.getAvatar());
        userInfo.setBio(request.getBio());
        userInfo.setBirthday(request.getBirthday());
        userInfo.setLocation(request.getLocation());
        userInfo.setEducation(request.getEducation());
        userInfo.setProfession(request.getProfession());

        UserInfo savedUserInfo = userInfoRepository.save(userInfo);

        return convertToProfileDTO(user, savedUserInfo);
    }

    private UserDTO convertToDTO(User user) {
        UserDTO dto = new UserDTO();
        dto.setId(user.getId());
        dto.setUsername(user.getUsername());
        dto.setCreatedAt(user.getCreatedAt());
        return dto;
    }

    private UserProfileDTO convertToProfileDTO(User user, UserInfo userInfo) {
        UserProfileDTO dto = new UserProfileDTO();

        dto.setUsername(user.getUsername());

        dto.setNickname(userInfo.getNickname());
        dto.setAvatar(userInfo.getAvatar());
        dto.setBio(userInfo.getBio());
        dto.setBirthday(userInfo.getBirthday());
        dto.setLocation(userInfo.getLocation());
        dto.setEducation(userInfo.getEducation());
        dto.setProfession(userInfo.getProfession());

        return dto;
    }
}
