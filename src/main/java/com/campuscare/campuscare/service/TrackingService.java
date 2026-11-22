package com.campuscare.campuscare.service;

import com.campuscare.campuscare.dto.CreateResponseRequest;
import com.campuscare.campuscare.dto.StatusUpdateRequest;
import com.campuscare.campuscare.dto.TimelineItemDto;
import com.campuscare.campuscare.model.*;
import com.campuscare.campuscare.repository.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.OffsetDateTime;
import java.util.*;

@Service
public class TrackingService {

    private final RequestRepository requestRepository;
    private final UserRepository userRepository;
    private final RequestStatusHistoryRepository historyRepository;
    private final ResponseRepository responseRepository;
    private final NotificationRepository notificationRepository;

    private static final Map<RequestStatus, Set<RequestStatus>> ALLOWED_TRANSITIONS = Map.of(
            RequestStatus.SUBMITTED,   Set.of(RequestStatus.ASSIGNED, RequestStatus.IN_PROGRESS, RequestStatus.REJECTED, RequestStatus.CANCELLED),
            RequestStatus.ASSIGNED,    Set.of(RequestStatus.IN_PROGRESS, RequestStatus.REJECTED, RequestStatus.CANCELLED),
            RequestStatus.IN_PROGRESS, Set.of(RequestStatus.RESOLVED, RequestStatus.REJECTED),
            RequestStatus.RESOLVED,    Set.of(RequestStatus.CLOSED, RequestStatus.IN_PROGRESS),
            RequestStatus.CLOSED,      Set.of(),
            RequestStatus.REJECTED,    Set.of(),
            RequestStatus.CANCELLED,   Set.of()
    );

    public TrackingService(
            RequestRepository requestRepository,
            UserRepository userRepository,
            RequestStatusHistoryRepository historyRepository,
            ResponseRepository responseRepository,
            NotificationRepository notificationRepository) {
        this.requestRepository = requestRepository;
        this.userRepository = userRepository;
        this.historyRepository = historyRepository;
        this.responseRepository = responseRepository;
        this.notificationRepository = notificationRepository;
    }

    @Transactional
    public RequestStatusHistory updateStatus(Long requestId, StatusUpdateRequest dto) {
        Request request = requestRepository.findById(requestId)
                .orElseThrow(() -> new IllegalArgumentException("Request not found: " + requestId));

        User actor = userRepository.findById(dto.changedByUserId())
                .orElseThrow(() -> new IllegalArgumentException("User not found: " + dto.changedByUserId()));

        RequestStatus currentStatus = request.getStatus();
        RequestStatus nextStatus = dto.newStatus();

        if (currentStatus == nextStatus) {
            throw new IllegalStateException("Request is already in status " + nextStatus);
        }

        Set<RequestStatus> validNext = ALLOWED_TRANSITIONS.getOrDefault(currentStatus, Set.of());
        if (!validNext.contains(nextStatus)) {
            throw new IllegalStateException("Cannot transition from " + currentStatus + " to " + nextStatus);
        }

        // Update request record
        request.setStatus(nextStatus);
        request.setUpdatedAt(OffsetDateTime.now());
        if (nextStatus == RequestStatus.RESOLVED) {
            request.setResolvedAt(OffsetDateTime.now());
        }
        requestRepository.save(request);

        // Save status history audit record
        RequestStatusHistory history = new RequestStatusHistory();
        history.setRequest(request);
        history.setOldStatus(currentStatus);
        history.setNewStatus(nextStatus);
        history.setChangedBy(actor);
        history.setNote(dto.note());
        RequestStatusHistory savedHistory = historyRepository.save(history);

        // Notify the student who submitted the request
        createNotification(
                request.getStudent(),
                request,
                "Your request #" + request.getRequestId() + " status changed from " + currentStatus + " to " + nextStatus
        );

        return savedHistory;
    }

    @Transactional
    public Response addResponse(Long requestId, CreateResponseRequest dto) {
        Request request = requestRepository.findById(requestId)
                .orElseThrow(() -> new IllegalArgumentException("Request not found: " + requestId));

        User author = userRepository.findById(dto.authorId())
                .orElseThrow(() -> new IllegalArgumentException("Author not found: " + dto.authorId()));

        Response response = new Response();
        response.setRequest(request);
        response.setAuthor(author);
        response.setMessage(dto.message().trim());
        response.setInternal(dto.isInternal());
        Response saved = responseRepository.save(response);

        request.setUpdatedAt(OffsetDateTime.now());
        requestRepository.save(request);

        // Only notify the student if the message is NOT an internal staff note
        if (!dto.isInternal() && !author.getUserId().equals(request.getStudent().getUserId())) {
            createNotification(
                    request.getStudent(),
                    request,
                    "New response from " + author.getFullName() + " on request #" + request.getRequestId()
            );
        }

        return saved;
    }

    @Transactional(readOnly = true)
    public List<TimelineItemDto> getRequestTimeline(Long requestId, boolean includeInternalNotes) {
        if (!requestRepository.existsById(requestId)) {
            throw new IllegalArgumentException("Request not found: " + requestId);
        }

        List<TimelineItemDto> timeline = new ArrayList<>();

        for (RequestStatusHistory h : historyRepository.findByRequestRequestIdOrderByChangedAtAsc(requestId)) {
            timeline.add(new TimelineItemDto(
                    "STATUS_CHANGE",
                    h.getChangedBy().getFullName(),
                    h.getOldStatus() != null ? h.getOldStatus().name() : null,
                    h.getNewStatus().name(),
                    h.getNote(),
                    false,
                    h.getChangedAt()
            ));
        }

        List<Response> responses = includeInternalNotes
                ? responseRepository.findByRequestRequestIdOrderByCreatedAtAsc(requestId)
                : responseRepository.findByRequestRequestIdAndInternalFalseOrderByCreatedAtAsc(requestId);

        for (Response r : responses) {
            timeline.add(new TimelineItemDto(
                    "RESPONSE",
                    r.getAuthor().getFullName(),
                    null,
                    null,
                    r.getMessage(),
                    r.isInternal(),
                    r.getCreatedAt()
            ));
        }

        timeline.sort(Comparator.comparing(TimelineItemDto::timestamp));
        return timeline;
    }

    @Transactional(readOnly = true)
    public List<Notification> getUserNotifications(Long userId) {
        return notificationRepository.findByUserUserIdOrderByCreatedAtDesc(userId);
    }

    @Transactional
    public void markNotificationAsRead(Long notificationId) {
        Notification notification = notificationRepository.findById(notificationId)
                .orElseThrow(() -> new IllegalArgumentException("Notification not found: " + notificationId));
        notification.setRead(true);
        notificationRepository.save(notification);
    }

    private void createNotification(User recipient, Request request, String text) {
        String trimmedMessage = text.length() > 255 ? text.substring(0, 255) : text;
        Notification notification = new Notification();
        notification.setUser(recipient);
        notification.setRequest(request);
        notification.setMessage(trimmedMessage);
        notificationRepository.save(notification);
    }
}