package com.campuscare.campuscare.dto;

import java.time.OffsetDateTime;

public record UserResponse(
        Long userId,
        String fullName,
        String email,
        String phone,
        String admissionNo,
        String role,
        String department,
        boolean active,
        OffsetDateTime createdAt
) {}