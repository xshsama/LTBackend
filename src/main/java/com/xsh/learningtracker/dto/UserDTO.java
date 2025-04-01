package com.xsh.learningtracker.dto;

import java.time.LocalDateTime;

import lombok.Data;

@Data
public class UserDTO {
    private Integer id;
    private String username;
    private LocalDateTime createdAt;
}