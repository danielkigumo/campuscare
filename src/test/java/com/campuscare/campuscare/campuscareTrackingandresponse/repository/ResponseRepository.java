// ResponseRepository.java
package com.campuscare.campuscare.repository;

import com.campuscare.campuscare.model.Response;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface ResponseRepository extends JpaRepository<Response, Long> {
    // Public replies visible to students
    List<Response> findByRequestRequestIdAndInternalFalseOrderByCreatedAtAsc(Long requestId);

    // All replies including internal staff notes
    List<Response> findByRequestRequestIdOrderByCreatedAtAsc(Long requestId);
}
