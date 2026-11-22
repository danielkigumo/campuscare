package com.campuscare.campuscare.dto;

import com.campuscare.campuscare.model.RequestStatus;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record StatusUpdateRequest(
        @NotNull(message = "New status is required") RequestStatus newStatus,
        @NotNull(message = "User ID is required") Long changedByUserId,
        @Size(max = 255, message = "Note cannot exceed 255 characters") String note
) {}