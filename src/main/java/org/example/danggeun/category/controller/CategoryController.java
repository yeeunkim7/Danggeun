// src/main/java/org/example/danggeun/category/controller/CategoryController.java
package org.example.danggeun.category.controller;

import lombok.RequiredArgsConstructor;
import org.example.danggeun.category.dto.CategoryCreateRequestDto;
import org.example.danggeun.category.dto.CategoryDto;
import org.example.danggeun.category.service.CategoryService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/categories")
@RequiredArgsConstructor
public class CategoryController {

    private final CategoryService categoryService;

    /** 전체 카테고리 조회 → GET /api/categories */
    @GetMapping
    public List<CategoryDto> listAll() {
        return categoryService.findAll()
                .stream()
                .map(cat -> new CategoryDto(cat.getId(), cat.getName()))
                .collect(Collectors.toList());
    }

    @PostMapping
    public ResponseEntity<CategoryDto> create(@RequestBody CategoryCreateRequestDto req) {
        // 이름 중복 검사
        if (categoryService.existsByName(req.getName())) {
            return ResponseEntity
                    .badRequest()
                    .build();
        }
        CategoryDto created = categoryService.create(req);
        return ResponseEntity.ok(created);
    }
}
