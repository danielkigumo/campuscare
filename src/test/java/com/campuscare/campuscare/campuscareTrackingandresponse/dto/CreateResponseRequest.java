// CreateResponseRequest.java
package com.campuscare.campuscare.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record CreateResponseRequest(
        @NotNull(message = "Author ID is required") Long authorId,
        @NotBlank(message = "Response message cannot be empty") String message,
        boolean isInternal
) {}
