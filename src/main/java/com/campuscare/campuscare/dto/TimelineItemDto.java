package com.campuscare.campuscare.dto;

import java.time.OffsetDateTime;

public record TimelineItemDto(
        String type,
        String actorName,
        String oldStatus,
        String newStatus,
        String message,
        boolean isInternal,
        OffsetDateTime timestamp
) {}