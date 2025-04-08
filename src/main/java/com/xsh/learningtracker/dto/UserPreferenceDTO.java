package com.xsh.learningtracker.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserPreferenceDTO {
    // Fields corresponding to the entity and frontend settings page
    private String theme;
    private Boolean emailNotifications;
    private String taskReminderFrequency;
    private Boolean communityUpdatesEnabled;
    private Boolean achievementNotificationsEnabled;
    private String defaultPage;
    private Boolean fixedSidebarEnabled; // Renamed from sidebarFixed
    private String language;

    // Existing fields (kept for compatibility, but not used in current settings
    // page)
    private Boolean showWelcome;
    private String statsViewMode;
    private Integer itemsPerPage;
}
