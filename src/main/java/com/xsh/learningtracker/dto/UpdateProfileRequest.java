package com.xsh.learningtracker.dto;

import java.time.LocalDate;

import jakarta.validation.constraints.Size;
import lombok.Data;

/**
 * 用户个人信息更新请求
 */
@Data
public class UpdateProfileRequest {

    @Size(max = 50, message = "昵称不能超过50个字符")
    private String nickname;

    private String avatar;

    @Size(max = 500, message = "个人简介不能超过500个字符")
    private String bio;

    private LocalDate birthday;

    @Size(max = 100, message = "位置不能超过100个字符")
    private String location;

    @Size(max = 200, message = "教育背景不能超过200个字符")
    private String education;

    @Size(max = 100, message = "职业信息不能超过100个字符")
    private String profession;
}