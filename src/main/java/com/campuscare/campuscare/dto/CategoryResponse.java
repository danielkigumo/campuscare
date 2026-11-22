package com.campuscare.campuscare.dto;

import java.time.OffsetDateTime;

public record CategoryResponse(
        Long categoryId,
        String name,
        String description,
        Long departmentId,
        String departmentName,
        boolean active
) {}