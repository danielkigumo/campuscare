// RequestStatusHistoryRepository.java
package com.campuscare.campuscare.repository;

import com.campuscare.campuscare.model.RequestStatusHistory;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface RequestStatusHistoryRepository extends JpaRepository<RequestStatusHistory, Long> {
    List<RequestStatusHistory> findByRequestRequestIdOrderByChangedAtAsc(Long requestId);
}
