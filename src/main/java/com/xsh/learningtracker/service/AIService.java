package com.xsh.learningtracker.service;

import com.xsh.learningtracker.dto.LearningReportDTO;

public interface AIService {

    /**
     * Generates a textual summary and suggestions based on structured learning
     * report data.
     *
     * @param reportData The structured learning report data.
     * @return A string containing the AI-generated summary and suggestions.
     * @throws Exception if an error occurs during communication with the AI
     *                   service.
     */
    String generateReportSummary(LearningReportDTO reportData) throws Exception;

    /**
     * Generates a textual summary based on a provided prompt string.
     * This can be used if the prompt is constructed externally.
     *
     * @param prompt The full prompt string to send to the AI model.
     * @return A string containing the AI-generated summary.
     * @throws Exception if an error occurs during communication with the AI
     *                   service.
     */
    String generateReportSummary(String prompt) throws Exception;
}