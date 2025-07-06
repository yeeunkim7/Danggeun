package org.example.danggeun.controller;

import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.example.danggeun.controller.Dto.TradeDto;
import org.example.danggeun.controller.Dto.TradeForm;
import org.example.danggeun.service.TradeService;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.ui.Model;

@Controller
@RequiredArgsConstructor
public class TradeController {

    private final TradeService tradeService;

    @PostMapping("/trade/submit")
    public String submit(@ModelAttribute TradeForm form, HttpSession session) {
        tradeService.submitTrade(form, session);
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