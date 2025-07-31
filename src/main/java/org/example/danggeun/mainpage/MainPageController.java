package org.example.danggeun.mainpage;

import lombok.RequiredArgsConstructor;
import org.example.danggeun.trade.dto.TradeListResponseDto;
import org.example.danggeun.trade.entity.Trade;
import org.example.danggeun.trade.service.TradeService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.List;

@Controller
@RequiredArgsConstructor
public class MainPageController {

    private final TradeService tradeService;

    @GetMapping("/")
    public String showMainPage(Model model) {
        List<TradeListResponseDto> items = tradeService.findAllProducts();
        model.addAttribute("itemList", items);
        return "mainPage/mainPage";
    }
}
