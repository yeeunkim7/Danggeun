package org.example.danggeun.mainpage.controller;

import io.micrometer.common.util.internal.logging.InternalLogger;
import lombok.RequiredArgsConstructor;
import org.example.danggeun.item.dto.ItemDto;
import org.example.danggeun.trade.dto.ProductListResponseDto;
import org.example.danggeun.trade.service.TradeService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.ArrayList;
import java.util.List;

@Controller
@RequiredArgsConstructor
public class mainPageController {
    private final TradeService tradeService;

    @GetMapping("/")
    public String showMainPage(Model model) {
        // 인기매물용 리스트 가져오기 (페이징 없이 가장 간단하게)
        List<ProductListResponseDto> items = tradeService.findAllProducts();
        model.addAttribute("itemList", items);
        return "mainPage/mainPage";  // 메인 페이지 템플릿 이름
    }

}
