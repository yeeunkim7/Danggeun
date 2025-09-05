package org.example.danggeun.category.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.danggeun.category.dto.CategoryCreateRequestDto;
import org.example.danggeun.category.dto.CategoryDto;
import org.example.danggeun.category.service.CategoryService;
import org.example.danggeun.common.dto.ApiResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@RestController
@RequestMapping("/api/categories")
@RequiredArgsConstructor
public class CategoryController {

    private final CategoryService categoryService;

    @GetMapping
    public ResponseEntity<ApiResponse<List<CategoryDto>>> getAllCategories() {
        try {
            List<CategoryDto> categories = categoryService.findAll().stream()
                    .map(c -> new CategoryDto(c.getId(), c.getName()))
                    .collect(Collectors.toList());

            return ResponseEntity.ok(
                    ApiResponse.<List<CategoryDto>>builder()
                            .success(true)
                            .data(categories)
                            .message("카테고리 목록 조회 성공")
                            .build()
            );
        } catch (Exception e) {
            log.error("카테고리 목록 조회 실패", e);
            return ResponseEntity.internalServerError().body(
                    ApiResponse.<List<CategoryDto>>builder()
                            .success(false)
                            .message("카테고리 목록 조회에 실패했습니다")
                            .build()
            );
        }
    }

    @PostMapping
    public ResponseEntity<ApiResponse<CategoryDto>> createCategory(@RequestBody CategoryCreateRequestDto request) {
        try {
            log.info("새 카테고리 생성 요청: {}", request.getName());

            // 입력값 유효성 검사
            if (request.getName() == null || request.getName().trim().isEmpty()) {
                return ResponseEntity.badRequest().body(
                        ApiResponse.<CategoryDto>builder()
                                .success(false)
                                .message("카테고리 이름은 필수입니다")
                                .build()
                );
            }

            if (request.getName().length() > 50) {
                return ResponseEntity.badRequest().body(
                        ApiResponse.<CategoryDto>builder()
                                .success(false)
                                .message("카테고리 이름은 50자를 초과할 수 없습니다")
                                .build()
                );
            }

            // 중복 이름 체크
            boolean exists = categoryService.existsByName(request.getName().trim());
            if (exists) {
                return ResponseEntity.badRequest().body(
                        ApiResponse.<CategoryDto>builder()
                                .success(false)
                                .message("이미 존재하는 카테고리 이름입니다")
                                .build()
                );
            }

            // 카테고리 생성 (CategoryService.create 메서드 사용)
            CategoryDto categoryDto = categoryService.create(request);

            log.info("카테고리 생성 성공: ID={}, 이름={}", categoryDto.getId(), categoryDto.getName());

            return ResponseEntity.ok(
                    ApiResponse.<CategoryDto>builder()
                            .success(true)
                            .data(categoryDto)
                            .message("카테고리가 성공적으로 생성되었습니다")
                            .build()
            );

        } catch (IllegalArgumentException e) {
            log.warn("잘못된 카테고리 생성 요청: {}", e.getMessage());
            return ResponseEntity.badRequest().body(
                    ApiResponse.<CategoryDto>builder()
                            .success(false)
                            .message(e.getMessage())
                            .build()
            );
        } catch (Exception e) {
            log.error("카테고리 생성 실패", e);
            return ResponseEntity.internalServerError().body(
                    ApiResponse.<CategoryDto>builder()
                            .success(false)
                            .message("카테고리 생성에 실패했습니다")
                            .build()
            );
        }
    }

    @DeleteMapping("/{categoryId}")
    public ResponseEntity<ApiResponse<Void>> deleteCategory(@PathVariable Long categoryId) {
        try {
            // categoryId 유효성 검사
            if (categoryId == null || categoryId <= 0) {
                return ResponseEntity.badRequest().body(
                        ApiResponse.<Void>builder()
                                .success(false)
                                .message("유효하지 않은 카테고리 ID입니다")
                                .build()
                );
            }

            // CategoryService에 deleteCategory 메서드가 없는 경우를 대비한 임시 처리
            // 실제로는 CategoryService에 deleteCategory 메서드를 추가해야 함
            /*
            categoryService.deleteCategory(categoryId);
            log.info("카테고리 삭제 성공: ID={}", categoryId);

            return ResponseEntity.ok(
                    ApiResponse.<Void>builder()
                            .success(true)
                            .message("카테고리가 성공적으로 삭제되었습니다")
                            .build()
            );
            */

            // 임시로 기능 미구현 응답
            return ResponseEntity.ok(
                    ApiResponse.<Void>builder()
                            .success(false)
                            .message("카테고리 삭제 기능은 현재 구현 중입니다")
                            .build()
            );

        } catch (IllegalArgumentException e) {
            log.warn("존재하지 않는 카테고리 삭제 시도: {}", categoryId);
            return ResponseEntity.badRequest().body(
                    ApiResponse.<Void>builder()
                            .success(false)
                            .message(e.getMessage())
                            .build()
            );
        } catch (Exception e) {
            log.error("카테고리 삭제 실패: ID={}", categoryId, e);
            return ResponseEntity.internalServerError().body(
                    ApiResponse.<Void>builder()
                            .success(false)
                            .message("카테고리 삭제에 실패했습니다")
                            .build()
            );
        }
    }
}