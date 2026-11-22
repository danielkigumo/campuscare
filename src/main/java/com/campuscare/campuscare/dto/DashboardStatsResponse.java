package com.campuscare.campuscare.dto;

import java.time.OffsetDateTime;

public record DashboardStatsResponse(
        long totalRequests,
        long submittedRequests,
        long assignedRequests,
        long inProgressRequests,
        long resolvedRequests,
        long closedRequests,
        long rejectedRequests,
        long cancelledRequests,
        long totalUsers,
        long totalStudents,
        long totalStaff,
        long totalAdmins,
        long unreadNotifications
) {}