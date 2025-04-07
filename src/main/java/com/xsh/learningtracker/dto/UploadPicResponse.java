package com.xsh.learningtracker.dto;

import lombok.Data;
import java.util.Map;

/**
 * 图片上传响应DTO
 */
@Data
public class UploadPicResponse {
    private ImageData data;
    private boolean success;
    private int status;

    @Data
    public static class ImageData {
        private String id;
        private String title;
        private String url_viewer;
        private String url;
        private String display_url;
        private String width;
        private String height;
        private String size;
        private String time;
        private String expiration;
        private Map<String, String> image;
        private Map<String, String> thumb;
        private Map<String, String> medium;
        private String delete_url;
    }
}
