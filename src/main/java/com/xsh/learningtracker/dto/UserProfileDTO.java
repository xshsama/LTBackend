package com.xsh.learningtracker.dto;

import java.time.LocalDate;

import lombok.Data;

@Data
public class UserProfileDTO {
    private Integer id; // 将用户ID字段类型修改为Integer
    private String username; // 来自 User
    private String nickname; // 来自 UserInfo
    private String avatar; // 来自 UserInfo
    private String bio; // 来自 UserInfo
    private LocalDate birthday; // 来自 UserInfo
    private String location; // 来自 UserInfo
    private String education; // 来自 UserInfo
    private String profession; // 来自 UserInfo
    private LocalDate createdAt; // 用户的注册时间
}