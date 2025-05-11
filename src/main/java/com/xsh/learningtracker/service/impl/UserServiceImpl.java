package com.xsh.learningtracker.service.impl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.xsh.learningtracker.dto.LoginRequest;
import com.xsh.learningtracker.dto.LoginResponseDTO;
import com.xsh.learningtracker.dto.RegisterRequest;
import com.xsh.learningtracker.dto.UpdatePasswordRequest;
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

    private static final Logger logger = LoggerFactory.getLogger(UserServiceImpl.class);

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

        // 先保存User实体
        User savedUser = userRepository.save(user);

        // 创建UserInfo并设置关联
        UserInfo userInfo = new UserInfo();
        userInfo.setUser(savedUser); // 设置user关联，这是关键步骤
        userInfo.setCreatedAt(java.time.Instant.ofEpochMilli(System.currentTimeMillis())
                .atZone(java.time.ZoneId.systemDefault())
                .toLocalDate());

        // 保存UserInfo
        userInfoRepository.save(userInfo);

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

        logger.info("正在获取用户 {} 的个人资料", username);
        User user = findByUsername(username);
        UserInfo userInfo = userInfoRepository.findByUser(user)
                .orElse(new UserInfo());

        // 将User和UserInfo转换为UserProfileDTO
        return convertToProfileDTO(user, userInfo);
    }

    @Override
    @Transactional
    public UserProfileDTO updateUserProfile(String username, UpdateProfileRequest request) {
        logger.info("正在更新用户 {} 的个人资料", username);
        logger.debug("更新请求数据: {}", request);

        User user = findByUsername(username);
        UserInfo userInfo = userInfoRepository.findByUser(user)
                .orElse(new UserInfo());

        if (userInfo.getId() == null) {
            userInfo.setUser(user);
            userInfo.setCreatedAt(java.time.LocalDate.now());
            logger.info("为用户 {} 创建新的个人资料", username);
        }

        // 只更新非null字段
        if (request.getNickname() != null)
            userInfo.setNickname(request.getNickname());
        if (request.getAvatar() != null)
            userInfo.setAvatar(request.getAvatar());
        if (request.getBio() != null)
            userInfo.setBio(request.getBio());
        if (request.getBirthday() != null)
            userInfo.setBirthday(request.getBirthday());
        if (request.getLocation() != null)
            userInfo.setLocation(request.getLocation());
        if (request.getEducation() != null)
            userInfo.setEducation(request.getEducation());
        if (request.getProfession() != null)
            userInfo.setProfession(request.getProfession());

        UserInfo savedUserInfo = userInfoRepository.save(userInfo);
        logger.info("用户 {} 的个人资料已更新", username);

        return convertToProfileDTO(user, savedUserInfo);
    }

    @Override
    @Transactional
    public boolean updatePassword(@AuthenticationPrincipal UserDetails userDetails, UpdatePasswordRequest request) {
        try {
            // 获取当前用户
            String username = userDetails.getUsername();
            User user = findByUsername(username);

            // 验证当前密码是否正确
            if (!passwordEncoder.matches(request.getCurrentPassword(), user.getPassword())) {
                return false; // 当前密码不匹配
            }

            // 验证新密码与确认密码是否一致
            if (!request.getNewPassword().equals(request.getConfirmPassword())) {
                return false; // 新密码与确认密码不一致
            }

            // 更新密码
            user.setPassword(passwordEncoder.encode(request.getNewPassword()));
            userRepository.save(user);

            return true;
        } catch (Exception e) {
            return false;
        }
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

        dto.setId(user.getId()); // 设置用户ID
        dto.setUsername(user.getUsername());
        // 避免设置null值
        dto.setNickname(userInfo.getNickname() != null ? userInfo.getNickname() : user.getUsername());
        dto.setAvatar(userInfo.getAvatar());
        dto.setBio(userInfo.getBio());
        dto.setBirthday(userInfo.getBirthday());
        dto.setLocation(userInfo.getLocation());
        dto.setEducation(userInfo.getEducation());
        dto.setProfession(userInfo.getProfession());

        // 设置用户注册时间，并处理类型转换
        if (userInfo.getCreatedAt() != null) {
            dto.setCreatedAt(userInfo.getCreatedAt());
        } else if (user.getCreatedAt() != null) {
            // 将User的LocalDateTime类型转换为LocalDate类型
            dto.setCreatedAt(user.getCreatedAt().toLocalDate());
        }

        return dto;
    }
}
