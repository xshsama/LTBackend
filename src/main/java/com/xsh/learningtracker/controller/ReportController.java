package com.xsh.learningtracker.controller;

import java.time.LocalDate;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.xsh.learningtracker.dto.LearningReportDTO; // Will be created later
import com.xsh.learningtracker.service.ReportService; // Will be created later
import com.xsh.learningtracker.util.JwtUtil; // Assuming you have JwtUtil for user ID

import jakarta.servlet.http.HttpServletRequest; // For JWT token to get user ID

@RestController
@RequestMapping("/api/reports")
public class ReportController {

    @Autowired
    private ReportService reportService;

    @Autowired
    private JwtUtil jwtUtil; // Assuming you use JwtUtil to extract user ID from token

    @GetMapping("/learning")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<?> generateLearningReport(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate,
            HttpServletRequest request) {

        String token = request.getHeader("Authorization");
        if (token == null || !token.startsWith("Bearer ")) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Missing or invalid Authorization token");
        }
        String jwt = token.substring(7);

        Integer userId;
        try {
            userId = jwtUtil.extractUserId(jwt);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid token: " + e.getMessage());
        }

        if (userId == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Could not extract user ID from token.");
        }

        try {
            LearningReportDTO report = reportService.generateLearningReport(userId, startDate, endDate);
            if (report == null) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body("No data found to generate report for the given criteria.");
            }
            return ResponseEntity.ok(report);
        } catch (Exception e) {
            // Log the exception e
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error generating learning report: " + e.getMessage());
        }
    }
}