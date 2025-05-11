package com.xsh.learningtracker.service;

import java.time.LocalDate;

import com.xsh.learningtracker.dto.LearningReportDTO;

public interface ReportService {
    /**
     * Generates a learning report for a given user within a specified date range.
     *
     * @param userId    The ID of the user for whom the report is generated.
     * @param startDate The start date of the reporting period (inclusive).
     * @param endDate   The end date of the reporting period (inclusive).
     * @return A LearningReportDTO containing the report data, or null if no data is
     *         found.
     * @throws Exception if an error occurs during report generation.
     */
    LearningReportDTO generateLearningReport(Integer userId, LocalDate startDate, LocalDate endDate) throws Exception;
}