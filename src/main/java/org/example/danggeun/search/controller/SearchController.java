package org.example.danggeun.search.controller;

import lombok.RequiredArgsConstructor;
import org.example.danggeun.search.dto.SearchResultDto;
import org.example.danggeun.search.service.SearchService;
import org.example.danggeun.search.type.SearchType;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
@RequiredArgsConstructor
public class SearchController {

    private final SearchService searchService;

    /**
     * 통합 검색 핸들러
     * - 검색어와 타입에 따라 상품, 사용자 또는 전체 검색 수행
     */
    @GetMapping("/search")
    public String search(
            @RequestParam("query") String keyword,
            @RequestParam(value = "type", defaultValue = "ALL") SearchType type,
            Pageable pageable,
            Model model
    ) {
        SearchResultDto result = searchService.search(keyword, type, pageable);

        model.addAttribute("result", result);      // 검색 결과 DTO (items + users)
        model.addAttribute("query", keyword);      // 검색어
        model.addAttribute("type", type);          // 검색 타입

        return "search/search"; // 결과를 보여줄 템플릿 (e.g., search.html)
    }
}
