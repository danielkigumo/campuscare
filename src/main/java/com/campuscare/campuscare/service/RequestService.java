package com.campuscare.campuscare.service;

import com.campuscare.campuscare.dto.CreateRequestRequest;
import com.campuscare.campuscare.dto.RequestResponse;
import com.campuscare.campuscare.dto.UpdateRequestRequest;
import com.campuscare.campuscare.model.Category;
import com.campuscare.campuscare.model.Request;
import com.campuscare.campuscare.model.RequestStatus;
import com.campuscare.campuscare.model.User;
import com.campuscare.campuscare.repository.CategoryRepository;
import com.campuscare.campuscare.repository.RequestRepository;
import com.campuscare.campuscare.repository.UserRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
public class RequestService {

    private final RequestRepository requestRepository;
    private final UserRepository userRepository;
    private final CategoryRepository categoryRepository;

    public RequestService(RequestRepository requestRepository,
                          UserRepository userRepository,
                          CategoryRepository categoryRepository) {
        this.requestRepository = requestRepository;
        this.userRepository = userRepository;
        this.categoryRepository = categoryRepository;
    }

    public RequestResponse createRequest(Long studentId, CreateRequestRequest dto) {
        User student = userRepository.findById(studentId)
                .orElseThrow(() -> new IllegalArgumentException("Student not found: " + studentId));

        Category category = categoryRepository.findById(dto.categoryId())
                .orElseThrow(() -> new IllegalArgumentException("Category not found: " + dto.categoryId()));

        if (!category.isActive()) {
            throw new IllegalArgumentException("Category is not active");
        }

        Request request = new Request();
        request.setStudent(student);
        request.setCategory(category);
        request.setRequestType(dto.requestType().toUpperCase());
        request.setTitle(dto.title().trim());
        request.setDescription(dto.description().trim());
        request.setPriority(dto.priority() != null ? dto.priority().toUpperCase() : "MEDIUM");
        request.setStatus(RequestStatus.SUBMITTED);
        request.setCreatedAt(OffsetDateTime.now());
        request.setUpdatedAt(OffsetDateTime.now());

        Request saved = requestRepository.save(request);
        return mapToResponse(saved);
    }

    public RequestResponse getRequestById(Long requestId) {
        Request request = requestRepository.findById(requestId)
                .orElseThrow(() -> new IllegalArgumentException("Request not found: " + requestId));
        return mapToResponse(request);
    }

    public List<RequestResponse> getRequestsByStudent(Long studentId) {
        return requestRepository.findByStudentUserId(studentId).stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    public Page<RequestResponse> getAllRequests(Pageable pageable) {
        return requestRepository.findAll(pageable).map(this::mapToResponse);
    }

    public List<RequestResponse> getRequestsByStatus(String status) {
        return requestRepository.findByStatusOrderByCreatedAtDesc(status).stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    public RequestResponse updateRequest(Long requestId, Long userId, UpdateRequestRequest dto) {
        Request request = requestRepository.findById(requestId)
                .orElseThrow(() -> new IllegalArgumentException("Request not found: " + requestId));

        // Check if user owns the request
        if (!request.getStudent().getUserId().equals(userId)) {
            throw new IllegalArgumentException("You can only update your own requests");
        }

        // Only allow updates if status is SUBMITTED or CANCELLED
        if (request.getStatus() != RequestStatus.SUBMITTED && request.getStatus() != RequestStatus.CANCELLED) {
            throw new IllegalStateException("Cannot update request in current status: " + request.getStatus());
        }

        if (dto.title() != null) {
            request.setTitle(dto.title().trim());
        }
        if (dto.description() != null) {
            request.setDescription(dto.description().trim());
        }
        if (dto.priority() != null) {
            request.setPriority(dto.priority().toUpperCase());
        }
        if (dto.categoryId() != null) {
            Category category = categoryRepository.findById(dto.categoryId())
                    .orElseThrow(() -> new IllegalArgumentException("Category not found: " + dto.categoryId()));
            request.setCategory(category);
        }
        request.setUpdatedAt(OffsetDateTime.now());

        Request saved = requestRepository.save(request);
        return mapToResponse(saved);
    }

    public void cancelRequest(Long requestId, Long userId) {
        Request request = requestRepository.findById(requestId)
                .orElseThrow(() -> new IllegalArgumentException("Request not found: " + requestId));

        if (!request.getStudent().getUserId().equals(userId)) {
            throw new IllegalArgumentException("You can only cancel your own requests");
        }

        if (request.getStatus() != RequestStatus.SUBMITTED) {
            throw new IllegalStateException("Can only cancel requests in SUBMITTED status");
        }

        request.setStatus(RequestStatus.CANCELLED);
        request.setUpdatedAt(OffsetDateTime.now());
        requestRepository.save(request);
    }

    private RequestResponse mapToResponse(Request request) {
        return new RequestResponse(
                request.getRequestId(),
                request.getStudent().getUserId(),
                request.getStudent().getFullName(),
                request.getStudent().getEmail(),
                request.getCategory().getCategoryId(),
                request.getCategory().getName(),
                request.getAssignedTo() != null ? request.getAssignedTo().getUserId() : null,
                request.getAssignedTo() != null ? request.getAssignedTo().getFullName() : null,
                request.getRequestType(),
                request.getTitle(),
                request.getDescription(),
                request.getPriority(),
                request.getStatus().name(),
                request.getCreatedAt(),
                request.getUpdatedAt(),
                request.getResolvedAt()
        );
    }
}