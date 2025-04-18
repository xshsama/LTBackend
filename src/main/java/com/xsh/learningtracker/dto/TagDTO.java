package com.xsh.learningtracker.dto;

import lombok.Data;

@Data
public class TagDTO {
    private Integer id;
    private String name;
    private String color;
    private Integer userId; // 对应Tag实体中的user.id
}
