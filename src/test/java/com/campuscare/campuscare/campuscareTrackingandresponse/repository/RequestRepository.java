// RequestRepository.java
package com.campuscare.campuscare.repository;

import com.campuscare.campuscare.model.Request;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RequestRepository extends JpaRepository<Request, Long> {
}
