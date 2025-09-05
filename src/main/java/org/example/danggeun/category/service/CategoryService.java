package org.example.danggeun.category.service;

import lombok.RequiredArgsConstructor;
import org.example.danggeun.category.dto.CategoryCreateRequestDto;
import org.example.danggeun.category.dto.CategoryDto;
import org.example.danggeun.category.entity.Category;
import org.example.danggeun.category.repository.CategoryRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CategoryService {

    private final CategoryRepository categoryRepository;

    @Transactional(readOnly = true)
    public List<Category> findAll() {
        return categoryRepository.findAll();
    }


    @Transactional(readOnly = true)
    public boolean existsByName(String name) {
        return categoryRepository.existsByName(name);
    }


    @Transactional
    public CategoryDto create(CategoryCreateRequestDto req) {
        // 유효성 검사
        if (req.getName() == null || req.getName().trim().isEmpty()) {
            throw new IllegalArgumentException("카테고리 이름은 필수입니다");
        }

        if (req.getName().length() > 50) {
            throw new IllegalArgumentException("카테고리 이름은 50자를 초과할 수 없습니다");
        }

        if (existsByName(req.getName().trim())) {
            throw new IllegalArgumentException("이미 존재하는 카테고리 이름입니다");
        }

        Category cat = Category.builder()
                .name(req.getName().trim())
                .build();
        Category saved = categoryRepository.save(cat);
        return new CategoryDto(saved.getId(), saved.getName());
    }

    @Transactional
    public void deleteCategory(Long categoryId) {
        Category category = categoryRepository.findById(categoryId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 카테고리입니다"));

        if (!category.getProducts().isEmpty()) {
            throw new IllegalArgumentException("상품이 존재하는 카테고리는 삭제할 수 없습니다");
        }

        categoryRepository.deleteById(categoryId);
    }

}