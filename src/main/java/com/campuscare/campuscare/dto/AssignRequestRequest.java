package com.campuscare.campuscare.dto;

import jakarta.validation.constraints.NotNull;

public record AssignRequestRequest(
        @NotNull(message = "Assignee ID is required") Long assigneeId
) {}