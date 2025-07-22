package org.example.danggeun.category.controller;

import lombok.RequiredArgsConstructor;
import org.example.danggeun.category.dto.CategoryCreateRequestDto;
import org.example.danggeun.category.entity.Category;
import org.example.danggeun.category.repository.CategoryRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController // @Controller 대신 @RestController 사용
@RequestMapping("/api/categories")
@RequiredArgsConstructor
public class CategoryController {

    private final CategoryRepository categoryRepository;

    @PostMapping
    public ResponseEntity<Category> createCategory(@RequestBody CategoryCreateRequestDto requestDto) {
        // 중복 카테고리 검사 (선택 사항이지만 권장)
        if (categoryRepository.existsByName(requestDto.getName())) {
            return ResponseEntity.badRequest().build(); // 이미 존재하면 400 Bad Request 응답
        }

        Category newCategory = Category.builder()
                .name(requestDto.getName())
                .build();

        Category savedCategory = categoryRepository.save(newCategory);

        // 성공 시, 생성된 카테고리 정보와 함께 200 OK 응답
        return ResponseEntity.ok(savedCategory);
    }
}
