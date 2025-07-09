package org.example.danggeun.trade.controller;

import jakarta.servlet.http.HttpSession;
import org.example.danggeun.trade.dto.TradeDto;
import org.example.danggeun.trade.service.TradeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

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

    @GetMapping("/trade/post")
    public String showTradePost(Model model, HttpSession session) {
        TradeDto dto = tradeService.getPostFromSession(session);

        if (dto == null) {
            return "redirect:/trade"; // 예: 글 목록 등으로 이동
        }

        model.addAttribute("dto", dto);
        return "trade/tradePost";
    }

    @PostMapping("/trade/submit")
    public String submitForm(@ModelAttribute TradeDto dto,
                             @RequestParam("productImage") MultipartFile file,
                             HttpSession session) {
        tradeService.submitAndStoreInSession(dto, file, session);
        return "redirect:/trade/post";
    }

    @PostMapping("/trade/chat")
    public String chat(HttpSession session) {
        tradeService.increaseChatInSession(session);
        return "redirect:/trade/post";
    }
}