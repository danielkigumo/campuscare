package com.campuscare.campuscare.controller;

import com.campuscare.campuscare.dto.CreateResponseRequest;
import com.campuscare.campuscare.dto.StatusUpdateRequest;
import com.campuscare.campuscare.dto.TimelineItemDto;
import com.campuscare.campuscare.model.Notification;
import com.campuscare.campuscare.model.RequestStatusHistory;
import com.campuscare.campuscare.model.Response;
import com.campuscare.campuscare.service.TrackingService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api")
public class TrackingController {

    private final TrackingService trackingService;

    public TrackingController(TrackingService trackingService) {
        this.trackingService = trackingService;
    }

    // 1. Update request status and record audit trail
    @PatchMapping("/requests/{requestId}/status")
    public ResponseEntity<?> updateRequestStatus(
            @PathVariable Long requestId,
            @Valid @RequestBody StatusUpdateRequest dto) {
        try {
            RequestStatusHistory history = trackingService.updateStatus(requestId, dto);
            return ResponseEntity.ok(Map.of(
                    "historyId", history.getHistoryId(),
                    "oldStatus", history.getOldStatus(),
                    "newStatus", history.getNewStatus(),
                    "changedAt", history.getChangedAt()
            ));
        } catch (IllegalArgumentException | IllegalStateException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    // 2. Post a response or internal staff note
    @PostMapping("/requests/{requestId}/responses")
    public ResponseEntity<?> postResponse(
            @PathVariable Long requestId,
            @Valid @RequestBody CreateResponseRequest dto) {
        try {
            Response response = trackingService.addResponse(requestId, dto);
            return ResponseEntity.ok(Map.of(
                    "responseId", response.getResponseId(),
                    "isInternal", response.isInternal(),
                    "createdAt", response.getCreatedAt()
            ));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    // 3. Fetch chronological timeline (status changes + responses)
    @GetMapping("/requests/{requestId}/timeline")
    public ResponseEntity<?> getTimeline(
            @PathVariable Long requestId,
            @RequestParam(defaultValue = "false") boolean staffView) {
        try {
            List<TimelineItemDto> timeline = trackingService.getRequestTimeline(requestId, staffView);
            return ResponseEntity.ok(timeline);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    // 4. Fetch notifications for a user
    @GetMapping("/users/{userId}/notifications")
    public ResponseEntity<List<Map<String, Object>>> getNotifications(@PathVariable Long userId) {
        List<Notification> list = trackingService.getUserNotifications(userId);
        List<Map<String, Object>> result = list.stream().map(n -> Map.<String, Object>of(
                "notificationId", n.getNotificationId(),
                "requestId", n.getRequest() != null ? n.getRequest().getRequestId() : 0L,
                "message", n.getMessage(),
                "isRead", n.isRead(),
                "createdAt", n.getCreatedAt()
        )).toList();
        return ResponseEntity.ok(result);
    }

    // 5. Mark a notification as read
    @PatchMapping("/notifications/{notificationId}/read")
    public ResponseEntity<?> markAsRead(@PathVariable Long notificationId) {
        try {
            trackingService.markNotificationAsRead(notificationId);
            return ResponseEntity.ok(Map.of("status", "marked_read"));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }
}
