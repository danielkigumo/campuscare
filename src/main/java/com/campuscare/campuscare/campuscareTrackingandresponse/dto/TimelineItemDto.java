// TimelineItemDto.java
package com.campuscare.campuscare.dto;

import java.time.OffsetDateTime;

public record TimelineItemDto(
        String type,          // "STATUS_CHANGE" or "RESPONSE"
        String actorName,
        String oldStatus,
        String newStatus,
        String message,
        boolean isInternal,
        OffsetDateTime timestamp
) {}
