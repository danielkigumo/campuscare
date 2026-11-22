package com.campuscare.campuscare.repository;

import com.campuscare.campuscare.model.Category;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.Optional;

public interface CategoryRepository extends JpaRepository<Category, Long> {
    List<Category> findByActiveTrue();
    Optional<Category> findByName(String name);
}