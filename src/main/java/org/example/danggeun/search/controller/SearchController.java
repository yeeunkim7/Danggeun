package org.example.danggeun.search.controller;

import lombok.RequiredArgsConstructor;
import org.example.danggeun.search.dto.SearchResultDto;
import org.example.danggeun.search.service.SearchService;
import org.example.danggeun.search.type.SearchType;
import org.springframework.data.domain.PageRequest;
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
            @RequestParam(value = "query", required = false, defaultValue = "") String query,
            @RequestParam(value = "type", required = false, defaultValue = "ALL") SearchType type,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "12") int size,
            Model model) {

        Pageable pageable = PageRequest.of(page, size);
        SearchResultDto result;

        if (query.isBlank()) {
            result = SearchResultDto.empty(); // 빈 결과 객체 반환
        } else {
            result = searchService.search(query, type, pageable);
        }

        model.addAttribute("query", query);
        model.addAttribute("type", type);
        model.addAttribute("result", result);

        return "search/search";
    }

}
