// NotificationRepository.java
package com.campuscare.campuscare.repository;

import com.campuscare.campuscare.model.Notification;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface NotificationRepository extends JpaRepository<Notification, Long> {
    List<Notification> findByUserUserIdOrderByCreatedAtDesc(Long userId);
    long countByUserUserIdAndReadFalse(Long userId);
}
