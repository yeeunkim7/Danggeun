package org.example.danggeun.trade.controller;

import jakarta.servlet.http.HttpSession;
import org.example.danggeun.trade.dto.TradeDto;
import org.example.danggeun.trade.entity.Trade;
import org.example.danggeun.trade.service.TradeService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

@Controller
public class TradeController {

    private TradeService tradeService;

    @GetMapping("/trade")
    public String showTradePage() {
        return "trade/trade";
    }

    @PostMapping("/trade/submit")
    public String submit(@ModelAttribute Trade trade, HttpSession session) {
        tradeService.submitTrade(trade, session);
        return "redirect:/trade/post";
    }

    @GetMapping("/trade/post")
    public String showTradePost(Model model, HttpSession session) {
        TradeDto dto = tradeService.getPost(session);
        model.addAttribute("dto", dto); // 전체 dto 하나로 전달
        return "tradePost";
    }

    @PostMapping("/trade/chat")
    public String chat(HttpSession session) {
        tradeService.increaseChat(session);
        return "redirect:/trade/post";
    }
}
