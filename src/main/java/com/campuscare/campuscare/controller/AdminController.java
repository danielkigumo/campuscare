package com.campuscare.campuscare.controller;

import com.campuscare.campuscare.dto.AssignRequestRequest;
import com.campuscare.campuscare.dto.DashboardStatsResponse;
import com.campuscare.campuscare.dto.RequestResponse;
import com.campuscare.campuscare.dto.UserResponse;
import com.campuscare.campuscare.service.AdminService;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/admin")
@PreAuthorize("hasRole('ADMIN') or hasRole('STAFF')")
public class AdminController {

    private final AdminService adminService;

    public AdminController(AdminService adminService) {
        this.adminService = adminService;
    }

    @GetMapping("/dashboard")
    public ResponseEntity<DashboardStatsResponse> getDashboardStats() {
        return ResponseEntity.ok(adminService.getDashboardStats());
    }

    @GetMapping("/requests")
    public ResponseEntity<Page<RequestResponse>> getAllRequests(
            @PageableDefault(size = 20) Pageable pageable) {
        return ResponseEntity.ok(adminService.getAllRequests(pageable));
    }

    @GetMapping("/requests/status/{status}")
    public ResponseEntity<List<RequestResponse>> getRequestsByStatus(@PathVariable String status) {
        return ResponseEntity.ok(adminService.getRequestsByStatus(status));
    }

    @PutMapping("/requests/{requestId}/assign")
    public ResponseEntity<?> assignRequest(
            @PathVariable Long requestId,
            @Valid @RequestBody AssignRequestRequest dto) {
        try {
            RequestResponse response = adminService.assignRequest(requestId, dto);
            return ResponseEntity.ok(response);
        } catch (IllegalArgumentException | IllegalStateException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    @GetMapping("/users")
    public ResponseEntity<Page<UserResponse>> getAllUsers(
            @PageableDefault(size = 20) Pageable pageable) {
        return ResponseEntity.ok(adminService.getAllUsers(pageable));
    }

    @GetMapping("/users/staff")
    public ResponseEntity<List<UserResponse>> getStaffUsers() {
        return ResponseEntity.ok(adminService.getStaffUsers());
    }
}