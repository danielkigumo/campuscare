package com.campuscare.campuscare.controller;

import com.campuscare.campuscare.dto.CategoryResponse;
import com.campuscare.campuscare.model.Category;
import com.campuscare.campuscare.repository.CategoryRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/categories")
public class CategoryController {

    private final CategoryRepository categoryRepository;

    public CategoryController(CategoryRepository categoryRepository) {
        this.categoryRepository = categoryRepository;
    }

    @GetMapping
    public ResponseEntity<List<CategoryResponse>> getAllCategories() {
        List<CategoryResponse> categories = categoryRepository.findAll().stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
        return ResponseEntity.ok(categories);
    }

    @GetMapping("/active")
    public ResponseEntity<List<CategoryResponse>> getActiveCategories() {
        List<CategoryResponse> categories = categoryRepository.findByActiveTrue().stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
        return ResponseEntity.ok(categories);
    }

    private CategoryResponse mapToResponse(Category category) {
        return new CategoryResponse(
                category.getCategoryId(),
                category.getName(),
                category.getDescription(),
                category.getDepartment() != null ? category.getDepartment().getDepartmentId() : null,
                category.getDepartment() != null ? category.getDepartment().getName() : null,
                category.isActive()
        );
    }
}