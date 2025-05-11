package com.xsh.learningtracker.service.impl;

import java.util.Collections;
import java.util.HashMap;
import java.util.List; // For messages list
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Primary; // Added for @Primary
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.xsh.learningtracker.dto.LearningReportDTO;
import com.xsh.learningtracker.service.AIService;

import lombok.extern.slf4j.Slf4j;

@Service("openRouterAIService")
@Primary // Mark this as the primary AIService implementation
@Slf4j
public class OpenRouterAIServiceImpl implements AIService {

    @Value("${openrouter.api.url:https://openrouter.ai/api/v1/chat/completions}")
    private String apiUrl;

    @Value("${openrouter.api.key}")
    private String apiKey;

    @Value("${openrouter.model.name:openai/gpt-3.5-turbo}") // Default to a common model
    private String defaultModel;

    @Value("${openrouter.http.referer:}") // Optional: Your site URL
    private String httpReferer;

    @Value("${openrouter.http.title:LearningTracker}") // Optional: Your site name
    private String httpTitle;

    private final RestTemplate restTemplate;
    private final ObjectMapper objectMapper;

    @Autowired
    public OpenRouterAIServiceImpl(RestTemplate restTemplate, ObjectMapper objectMapper) {
        this.restTemplate = restTemplate;
        this.objectMapper = objectMapper;
    }

    private String buildContentForOpenRouter(LearningReportDTO reportData) throws JsonProcessingException {
        // Serialize the DTO to JSON to be included in the prompt
        String reportDataJson = objectMapper.writeValueAsString(reportData);

        // Construct a detailed prompt that includes the JSON data
        return "您是一名专业的学习分析师。基于以下学习数据（JSON格式），" +
                "提供用户学习进度的简明摘要，突出关键成就，" +
                "确定潜在的改进领域，并提供2-3条可操作的建议。" +
                "保持积极和鼓舞人心的语气。总结应该结构清晰。\n\n”" +
                "Learning Data:\n" + reportDataJson;
    }

    @Override
    public String generateReportSummary(LearningReportDTO reportData) throws Exception {
        String content = buildContentForOpenRouter(reportData);
        return getChatCompletion(content, defaultModel); // Use the default model
    }

    @Override
    public String generateReportSummary(String prompt) throws Exception {
        // This method can be used if a pre-formatted prompt is already available
        return getChatCompletion(prompt, defaultModel); // Use the default model
    }

    private String getChatCompletion(String userPrompt, String modelName) throws Exception {
        log.info("Sending prompt to OpenRouter. Model: {}, Prompt length: {}", modelName, userPrompt.length());

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set("Authorization", "Bearer " + apiKey);
        if (httpReferer != null && !httpReferer.isEmpty()) {
            headers.set("HTTP-Referer", httpReferer);
        }
        if (httpTitle != null && !httpTitle.isEmpty()) {
            headers.set("X-Title", httpTitle);
        }

        Map<String, Object> message = new HashMap<>();
        message.put("role", "user");
        message.put("content", userPrompt);

        Map<String, Object> requestBody = new HashMap<>();
        requestBody.put("model", modelName);
        requestBody.put("messages", Collections.singletonList(message));
        // requestBody.put("max_tokens", 500); // Optional: to limit response length

        HttpEntity<Map<String, Object>> entity = new HttpEntity<>(requestBody, headers);

        try {
            ResponseEntity<String> response = restTemplate.postForEntity(apiUrl, entity, String.class);
            log.info("Received response from OpenRouter. Status: {}", response.getStatusCode());

            // Basic parsing assuming the response body is a JSON string containing the AI's
            // message.
            // OpenRouter's chat completion response is similar to OpenAI's.
            // Example: {"id": "...", "model": "...", "choices": [{"message": {"role":
            // "assistant", "content": "..."}}]}
            // We need to parse this JSON and extract choices[0].message.content
            if (response.getBody() != null) {
                Map<String, Object> responseMap = objectMapper.readValue(response.getBody(),
                        new com.fasterxml.jackson.core.type.TypeReference<Map<String, Object>>() {
                        });
                List<Map<String, Object>> choices = (List<Map<String, Object>>) responseMap.get("choices");
                if (choices != null && !choices.isEmpty()) {
                    Map<String, Object> firstChoice = choices.get(0);
                    Map<String, String> messageFromChoice = (Map<String, String>) firstChoice.get("message");
                    if (messageFromChoice != null && messageFromChoice.containsKey("content")) {
                        return messageFromChoice.get("content");
                    }
                }
                log.warn("OpenRouter response did not contain expected message content structure: {}",
                        response.getBody());
                throw new Exception("Failed to parse AI summary from OpenRouter response.");
            }
            throw new Exception("Received empty response body from OpenRouter.");
        } catch (Exception e) {
            log.error("Error communicating with OpenRouter API: {}", e.getMessage(), e);
            throw new Exception("Failed to generate AI summary via OpenRouter: " + e.getMessage(), e);
        }
    }
}