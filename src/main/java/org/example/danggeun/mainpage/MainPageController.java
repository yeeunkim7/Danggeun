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
        List<ProductListResponseDto> items = tradeService.findAllProducts();
        model.addAttribute("itemList", items);
        return "mainPage/mainPage";
    }

}
