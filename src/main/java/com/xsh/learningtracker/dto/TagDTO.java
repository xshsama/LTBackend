package com.xsh.learningtracker.dto;

import lombok.Data;

@Data
public class TagDTO {
    private Integer id;
    private String name;
    private String title; // 添加title字段，用于与前端对接
    private String color;
    private Integer userId; // 对应Tag实体中的user.id

    public TagDTO() {
    }

    public TagDTO(Integer id, String name, String color) {
        this.id = id;
        this.name = name;
        this.title = name; // 将name值同步给title
        this.color = color;
    }

    public TagDTO(Integer id, String name, String color, Integer userId) {
        this.id = id;
        this.name = name;
        this.title = name; // 将name值同步给title
        this.color = color;
        this.userId = userId;
    }
}
