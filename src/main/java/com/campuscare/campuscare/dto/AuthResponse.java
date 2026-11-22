package com.campuscare.campuscare.dto;

public record AuthResponse(
        String accessToken,
        String refreshToken,
        String tokenType,
        Long userId,
        String fullName,
        String email,
        String role
) {}