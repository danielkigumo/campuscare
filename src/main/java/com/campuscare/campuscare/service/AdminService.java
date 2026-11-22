package com.campuscare.campuscare.service;

import com.campuscare.campuscare.dto.AssignRequestRequest;
import com.campuscare.campuscare.dto.DashboardStatsResponse;
import com.campuscare.campuscare.dto.RequestResponse;
import com.campuscare.campuscare.dto.UserResponse;
import com.campuscare.campuscare.model.Request;
import com.campuscare.campuscare.model.RequestStatus;
import com.campuscare.campuscare.model.Role;
import com.campuscare.campuscare.model.User;
import com.campuscare.campuscare.repository.RequestRepository;
import com.campuscare.campuscare.repository.RoleRepository;
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
public class AdminService {

    private final RequestRepository requestRepository;
    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final RequestService requestService;

    public AdminService(RequestRepository requestRepository,
                        UserRepository userRepository,
                        RoleRepository roleRepository,
                        RequestService requestService) {
        this.requestRepository = requestRepository;
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.requestService = requestService;
    }

    public DashboardStatsResponse getDashboardStats() {
        long totalRequests = requestRepository.count();
        long submittedRequests = requestRepository.countByStatus(RequestStatus.SUBMITTED.name());
        long assignedRequests = requestRepository.countByStatus(RequestStatus.ASSIGNED.name());
        long inProgressRequests = requestRepository.countByStatus(RequestStatus.IN_PROGRESS.name());
        long resolvedRequests = requestRepository.countByStatus(RequestStatus.RESOLVED.name());
        long closedRequests = requestRepository.countByStatus(RequestStatus.CLOSED.name());
        long rejectedRequests = requestRepository.countByStatus(RequestStatus.REJECTED.name());
        long cancelledRequests = requestRepository.countByStatus(RequestStatus.CANCELLED.name());

        long totalUsers = userRepository.count();
        Role studentRole = roleRepository.findByName("STUDENT").orElse(null);
        Role staffRole = roleRepository.findByName("STAFF").orElse(null);
        Role adminRole = roleRepository.findByName("ADMIN").orElse(null);

        long totalStudents = studentRole != null ? userRepository.countByRoleRoleId(studentRole.getRoleId()) : 0;
        long totalStaff = staffRole != null ? userRepository.countByRoleRoleId(staffRole.getRoleId()) : 0;
        long totalAdmins = adminRole != null ? userRepository.countByRoleRoleId(adminRole.getRoleId()) : 0;

        return new DashboardStatsResponse(
                totalRequests, submittedRequests, assignedRequests, inProgressRequests,
                resolvedRequests, closedRequests, rejectedRequests, cancelledRequests,
                totalUsers, totalStudents, totalStaff, totalAdmins, 0L
        );
    }

    public Page<RequestResponse> getAllRequests(Pageable pageable) {
        return requestRepository.findAll(pageable).map(this::mapToRequestResponse);
    }

    public List<RequestResponse> getRequestsByStatus(String status) {
        return requestRepository.findByStatusOrderByCreatedAtDesc(status.toUpperCase()).stream()
                .map(this::mapToRequestResponse)
                .collect(Collectors.toList());
    }

    public RequestResponse assignRequest(Long requestId, AssignRequestRequest dto) {
        Request request = requestRepository.findById(requestId)
                .orElseThrow(() -> new IllegalArgumentException("Request not found: " + requestId));

        User assignee = userRepository.findById(dto.assigneeId())
                .orElseThrow(() -> new IllegalArgumentException("Assignee not found: " + dto.assigneeId()));

        if (request.getStatus() != RequestStatus.SUBMITTED && request.getStatus() != RequestStatus.ASSIGNED) {
            throw new IllegalStateException("Can only assign requests in SUBMITTED or ASSIGNED status");
        }

        request.setAssignedTo(assignee);
        request.setStatus(RequestStatus.ASSIGNED);
        request.setUpdatedAt(OffsetDateTime.now());
        requestRepository.save(request);

        return mapToRequestResponse(request);
    }

    public Page<UserResponse> getAllUsers(Pageable pageable) {
        return userRepository.findAll(pageable).map(this::mapToUserResponse);
    }

    public List<UserResponse> getStaffUsers() {
        Role staffRole = roleRepository.findByName("STAFF")
                .orElseThrow(() -> new IllegalArgumentException("STAFF role not found"));
        return userRepository.findByRoleRoleId(staffRole.getRoleId()).stream()
                .map(this::mapToUserResponse)
                .collect(Collectors.toList());
    }

    private RequestResponse mapToRequestResponse(Request request) {
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

    private UserResponse mapToUserResponse(User user) {
        return new UserResponse(
                user.getUserId(),
                user.getFullName(),
                user.getEmail(),
                user.getPhone(),
                user.getAdmissionNo(),
                user.getRole() != null ? user.getRole().getName() : "UNKNOWN",
                user.getDepartment() != null ? user.getDepartment().getName() : null,
                user.isActive(),
                user.getCreatedAt()
        );
    }
}