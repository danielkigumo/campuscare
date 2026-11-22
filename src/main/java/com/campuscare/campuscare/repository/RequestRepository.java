package com.campuscare.campuscare.repository;

import com.campuscare.campuscare.model.Request;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface RequestRepository extends JpaRepository<Request, Long> {
    List<Request> findByStudentUserId(Long studentId);
    List<Request> findByAssignedToUserId(Long assignedToId);
    List<Request> findByStatusOrderByCreatedAtDesc(String status);
    long countByStatus(String status);
}