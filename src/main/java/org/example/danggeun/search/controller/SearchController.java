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

import javax.naming.directory.SearchResult;

@Controller
@RequiredArgsConstructor
public class SearchController {

    private final SearchService searchService;

    /**
     * 통합 검색 핸들러
     * - 검색어와 타입에 따라 상품, 사용자 또는 전체 검색 수행
     */
    @GetMapping("/search")
    public String search(@RequestParam("query") String query,
                         @RequestParam("type") SearchType type,
                         @RequestParam(defaultValue = "0") int page,
                         @RequestParam(defaultValue = "12") int size,
                         Model model) {

        Pageable pageable = PageRequest.of(page, size);
        // SearchService에서 리턴된 결과를 변수에 저장
        SearchResultDto result = searchService.search(query, type, pageable);

        // 모델에 검색 조건 및 결과 추가
        model.addAttribute("query", query);
        model.addAttribute("type", type);
        model.addAttribute("result", result);

        return "search/search";
    }
}
