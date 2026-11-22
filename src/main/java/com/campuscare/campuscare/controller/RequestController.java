package com.campuscare.campuscare.controller;

import com.campuscare.campuscare.dto.CategoryResponse;
import com.campuscare.campuscare.dto.CreateRequestRequest;
import com.campuscare.campuscare.dto.RequestResponse;
import com.campuscare.campuscare.dto.UpdateRequestRequest;
import com.campuscare.campuscare.model.Category;
import com.campuscare.campuscare.model.Request;
import com.campuscare.campuscare.service.RequestService;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/requests")
public class RequestController {

    private final RequestService requestService;

    public RequestController(RequestService requestService) {
        this.requestService = requestService;
    }

    @PostMapping
    public ResponseEntity<?> createRequest(
            @AuthenticationPrincipal UserDetails userDetails,
            @Valid @RequestBody CreateRequestRequest dto) {
        try {
            // Extract userId from UserDetails (email)
            Long studentId = getUserIdFromPrincipal(userDetails);
            RequestResponse response = requestService.createRequest(studentId, dto);
            return ResponseEntity.ok(response);
        } catch (IllegalArgumentException | IllegalStateException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    @GetMapping("/my")
    public ResponseEntity<List<RequestResponse>> getMyRequests(
            @AuthenticationPrincipal UserDetails userDetails) {
        Long studentId = getUserIdFromPrincipal(userDetails);
        List<RequestResponse> requests = requestService.getRequestsByStudent(studentId);
        return ResponseEntity.ok(requests);
    }

    @GetMapping("/{requestId}")
    public ResponseEntity<?> getRequestById(
            @AuthenticationPrincipal UserDetails userDetails,
            @PathVariable Long requestId) {
        try {
            RequestResponse response = requestService.getRequestById(requestId);
            return ResponseEntity.ok(response);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    @PutMapping("/{requestId}")
    public ResponseEntity<?> updateRequest(
            @AuthenticationPrincipal UserDetails userDetails,
            @PathVariable Long requestId,
            @Valid @RequestBody UpdateRequestRequest dto) {
        try {
            Long userId = getUserIdFromPrincipal(userDetails);
            RequestResponse response = requestService.updateRequest(requestId, userId, dto);
            return ResponseEntity.ok(response);
        } catch (IllegalArgumentException | IllegalStateException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    @DeleteMapping("/{requestId}")
    public ResponseEntity<?> cancelRequest(
            @AuthenticationPrincipal UserDetails userDetails,
            @PathVariable Long requestId) {
        try {
            Long userId = getUserIdFromPrincipal(userDetails);
            requestService.cancelRequest(requestId, userId);
            return ResponseEntity.ok(Map.of("message", "Request cancelled successfully"));
        } catch (IllegalArgumentException | IllegalStateException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    // Admin/Staff endpoints
    @GetMapping
    public ResponseEntity<Page<RequestResponse>> getAllRequests(
            @PageableDefault(size = 20) Pageable pageable) {
        Page<RequestResponse> requests = requestService.getAllRequests(pageable);
        return ResponseEntity.ok(requests);
    }

    @GetMapping("/status/{status}")
    public ResponseEntity<List<RequestResponse>> getRequestsByStatus(@PathVariable String status) {
        List<RequestResponse> requests = requestService.getRequestsByStatus(status.toUpperCase());
        return ResponseEntity.ok(requests);
    }

    private Long getUserIdFromPrincipal(UserDetails userDetails) {
        // The UserPrincipal stores userId
        if (userDetails instanceof com.campuscare.campuscare.security.UserPrincipal) {
            return ((com.campuscare.campuscare.security.UserPrincipal) userDetails).getUserId();
        }
        throw new IllegalStateException("Invalid user principal");
    }
}