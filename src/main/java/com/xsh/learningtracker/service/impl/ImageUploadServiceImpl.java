package com.xsh.learningtracker.service.impl;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import com.xsh.learningtracker.dto.UploadPicRequest;
import com.xsh.learningtracker.dto.UploadPicResponse;
import com.xsh.learningtracker.service.ImageUploadService;

@Service
public class ImageUploadServiceImpl implements ImageUploadService {

    private final RestTemplate restTemplate;

    @Value("${imgbb.api.key}")
    private String apiKey;

    @Value("${imgbb.api.url:https://api.imgbb.com/1/upload}")
    private String apiUrl;

    public ImageUploadServiceImpl() {
        this.restTemplate = new RestTemplate();
    }

    @Override
    public UploadPicResponse uploadImage(UploadPicRequest request) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.MULTIPART_FORM_DATA);

        MultiValueMap<String, Object> body = new LinkedMultiValueMap<>();
        body.add("key", apiKey);
        body.add("image", request.getImage());

        if (request.getName() != null && !request.getName().isEmpty()) {
            body.add("name", request.getName());
        }

        if (request.getExpiration() != null) {
            body.add("expiration", request.getExpiration());
        }

        HttpEntity<MultiValueMap<String, Object>> requestEntity = new HttpEntity<>(body, headers);

        return restTemplate.postForObject(apiUrl, requestEntity, UploadPicResponse.class);
    }
}