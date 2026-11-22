package com.campuscare.campuscare.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record CreateRequestRequest(
        @NotNull(message = "Category ID is required") Long categoryId,
        @NotBlank(message = "Request type is required") String requestType,
        @NotBlank(message = "Title is required") @Size(min = 5, max = 150) String title,
        @NotBlank(message = "Description is required") @Size(min = 10) String description,
        String priority
) {}