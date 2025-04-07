package com.xsh.learningtracker.controller;

import java.io.IOException;
import java.util.Base64;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.xsh.learningtracker.dto.ApiResponse;
import com.xsh.learningtracker.dto.UploadPicRequest;
import com.xsh.learningtracker.dto.UploadPicResponse;
import com.xsh.learningtracker.entity.User;
import com.xsh.learningtracker.entity.UserInfo;
import com.xsh.learningtracker.repository.UserInfoRepository;
import com.xsh.learningtracker.repository.UserRepository;
import com.xsh.learningtracker.service.ImageUploadService;

@RestController
@RequestMapping("/api/avatar")
public class AvatarUploadController {

    private final ImageUploadService imageUploadService;
    private final UserRepository userRepository;
    private final UserInfoRepository userInfoRepository;

    public AvatarUploadController(ImageUploadService imageUploadService,
            UserRepository userRepository,
            UserInfoRepository userInfoRepository) {
        this.imageUploadService = imageUploadService;
        this.userRepository = userRepository;
        this.userInfoRepository = userInfoRepository;
    }

    /**
     * 上传Base64编码的图片
     */
    @PostMapping("/upload/base64")
    public ResponseEntity<ApiResponse<UploadPicResponse>> uploadBase64Image(@RequestBody UploadPicRequest request) {
        try {
            UploadPicResponse response = imageUploadService.uploadImage(request);
            return ResponseEntity.ok(ApiResponse.success("图片上传成功", response));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(ApiResponse.error(400, "图片上传失败: " + e.getMessage()));
        }
    }

    /**
     * 上传文件格式的图片
     */
    @PostMapping("/upload/file")
    public ResponseEntity<ApiResponse<UploadPicResponse>> uploadFileImage(
            @RequestParam("file") MultipartFile file,
            @RequestParam(value = "name", required = false) String name,
            @RequestParam(value = "expiration", required = false) Integer expiration,
            @AuthenticationPrincipal UserDetails userDetails) {

        try {
            // 将MultipartFile转换为Base64编码
            String base64Image = Base64.getEncoder().encodeToString(file.getBytes());

            // 构建请求对象
            UploadPicRequest request = new UploadPicRequest();
            request.setImage(base64Image);
            request.setName(name != null ? name : file.getOriginalFilename());
            request.setExpiration(expiration);

            // 调用图片上传服务
            UploadPicResponse response = imageUploadService.uploadImage(request);

            // 如果需要更新用户头像，请使用正确的方式查找和更新用户信息
            if (userDetails != null) {
                User user = userRepository.findByUsername(userDetails.getUsername())
                        .orElse(null);

                if (user != null) {
                    UserInfo userInfo = userInfoRepository.findByUser(user)
                            .orElse(null);

                    if (userInfo != null) {
                        userInfo.setAvatar(response.getData().getUrl());
                        userInfoRepository.save(userInfo);
                    }
                }
            }

            return ResponseEntity.ok(ApiResponse.success("图片上传成功", response));

        } catch (IOException e) {
            return ResponseEntity.badRequest().body(ApiResponse.error(400, "文件读取失败: " + e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(ApiResponse.error(400, "图片上传失败: " + e.getMessage()));
        }
    }
}