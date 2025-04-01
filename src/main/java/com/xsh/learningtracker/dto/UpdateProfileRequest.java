package com.xsh.learningtracker.dto;

import java.time.LocalDate;

import jakarta.validation.constraints.Past;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class UpdateProfileRequest {
    @Size(max = 30, message = "昵称不能超过30个字符")
    private String nickname;

    @Size(max = 200, message = "个人简介不能超过200个字符")
    private String bio;

    @Past(message = "生日必须是过去的日期")
    private LocalDate birthday;

    @Size(max = 100, message = "地址不能超过100个字符")
    private String location;

    @Size(max = 100, message = "教育信息不能超过100个字符")
    private String education;

    @Size(max = 100, message = "职业信息不能超过100个字符")
    private String profession;
}