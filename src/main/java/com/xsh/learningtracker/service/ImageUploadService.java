package com.xsh.learningtracker.service;

import com.xsh.learningtracker.dto.UploadPicRequest;
import com.xsh.learningtracker.dto.UploadPicResponse;

public interface ImageUploadService {

    /**
     * 上传图片到ImgBB
     * 
     * @param request 包含图片信息的请求
     * @return 图片上传响应
     */
    UploadPicResponse uploadImage(UploadPicRequest request);
}