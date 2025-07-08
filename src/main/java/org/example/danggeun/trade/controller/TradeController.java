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
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseBody;

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

        if (dto == null) {
            // 기본 페이지로 리디렉션하거나 오류 페이지로 이동
            return "redirect:/trade"; // 예: 글 목록 등으로 이동
        }

        model.addAttribute("dto", dto);
        return "trade/tradePost";
    }

    // 비동기 fetch POST용 API 메서드 (JSON 요청 처리)
    @PostMapping(value = "/trade/api/submit", consumes = "application/json")
    @ResponseBody
    public TradeDto apiSubmit(@RequestBody TradeDto tradeDto, HttpSession session) {
        tradeService.submitAndStoreInSession(tradeDto, session);
        return tradeDto;  // 저장된 DTO를 JSON으로 응답
    }

    @PostMapping("/trade/chat")
    public String chat(HttpSession session) {
        tradeService.increaseChatInSession(session);
        return "redirect:/trade/post";
    }
}