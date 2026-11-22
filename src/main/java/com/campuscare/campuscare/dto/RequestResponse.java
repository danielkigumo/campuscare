package com.campuscare.campuscare.dto;

import java.time.OffsetDateTime;

public record RequestResponse(
        Long requestId,
        Long studentId,
        String studentName,
        String studentEmail,
        Long categoryId,
        String categoryName,
        Long assignedToId,
        String assignedToName,
        String requestType,
        String title,
        String description,
        String priority,
        String status,
        OffsetDateTime createdAt,
        OffsetDateTime updatedAt,
        OffsetDateTime resolvedAt
) {}