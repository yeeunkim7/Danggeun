package org.example.danggeun.trade.controller;

import jakarta.servlet.http.HttpSession;
import org.example.danggeun.trade.dto.TradeDto;
import org.example.danggeun.trade.service.TradeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

@Controller
public class TradeController {

    private final TradeService tradeService;

    @Autowired
    public TradeController(TradeService tradeService) {
        this.tradeService = tradeService;
    }

    @GetMapping("/trade")
    public String showTradePage() {
        return "trade/trade"; // templates/trade/trade.html
    }

    @PostMapping("/trade/submit")
    public String submit(@ModelAttribute TradeDto tradeDto, HttpSession session) {
        // 엔티티가 아닌 DTO를 서비스로 전달
        tradeService.submitAndStoreInSession(tradeDto, session);
        return "redirect:/trade/post";
    }

    @GetMapping("/trade/post")
    public String showTradePost(Model model, HttpSession session) {
        TradeDto dto = tradeService.getPostFromSession(session);
        model.addAttribute("dto", dto);
        return "trade/tradePost"; // templates/trade/tradePost.html
    }

    @PostMapping("/trade/chat")
    public String chat(HttpSession session) {
        tradeService.increaseChatInSession(session);
        return "redirect:/trade/post";
    }
}