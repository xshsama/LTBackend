package com.xsh.learningtracker.dto;

import lombok.Data;

/**
 * 图片上传请求DTO
 */
@Data
public class UploadPicRequest {
    // 图片base64编码内容
    private String image;

    // 图片名称（可选）
    private String name;

    // 图片过期时间（可选，单位为秒）
    private Integer expiration;
}
