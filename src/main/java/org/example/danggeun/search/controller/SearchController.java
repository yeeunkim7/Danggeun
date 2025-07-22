package org.example.danggeun.search.controller;

import lombok.RequiredArgsConstructor;
import org.example.danggeun.trade.dto.ProductListResponseDto;
import org.example.danggeun.trade.service.TradeService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
@RequiredArgsConstructor
public class SearchController {

    private final TradeService tradeService;

    @GetMapping("/search")
    public String searchItems(
            @RequestParam(value = "query", required = false) String query,
            Pageable pageable,
            Model model
    ) {
        Page<ProductListResponseDto> page;
        if (query != null && !query.isBlank()) {
            page = tradeService.searchProducts(query, pageable);
        } else {
            page = tradeService.findAllProducts(pageable);
        }
        model.addAttribute("page", page);
        model.addAttribute("query", query);
        return "search/search";
    }
}