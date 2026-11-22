package com.campuscare.campuscare.dto;

import jakarta.validation.constraints.Size;

public record UpdateRequestRequest(
        @Size(min = 5, max = 150) String title,
        @Size(min = 10) String description,
        String priority,
        Long categoryId
) {}