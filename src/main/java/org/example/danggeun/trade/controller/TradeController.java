package org.example.danggeun.trade.controller;

import jakarta.servlet.http.HttpSession;
import org.example.danggeun.trade.dto.TradeDto;
import org.example.danggeun.trade.service.TradeService;
import org.example.danggeun.write.repository.WriteRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.example.danggeun.write.entity.Write;

@Controller
public class TradeController {
    private final WriteRepository writeRepository;
    private final TradeService tradeService;

    @Autowired
    public TradeController(WriteRepository writeRepository, TradeService tradeService) {
        this.writeRepository = writeRepository;
        this.tradeService = tradeService;
    }

    @GetMapping("/trade")
    public String showTradePage() {
        return "trade/trade"; // templates/trade/trade.html
    }

    @GetMapping("/trade/post/{id}")
    public String showPostDetail(@PathVariable Long id, Model model) {
        Write post = writeRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("게시글 없음"));
        model.addAttribute("dto", post);
        return "trade/tradePost";  // templates/trade/tradePost.html
    }

    // 비동기 fetch POST용 API 메서드 (JSON 요청 처리)
    @PostMapping(value = "/trade/submit", consumes = "multipart/form-data")
    public String apiSubmit(
            @ModelAttribute TradeDto tradeDto,
            @RequestParam("productImage") MultipartFile file,
            HttpSession session
    ) {
        Long savedId = tradeService.submitAndStoreInSession(tradeDto, file, session);
        System.out.println("저장된 ID: " + savedId);
        return "redirect:/trade/post/" + savedId;  // ID 포함해서 리다이렉트
    }

    @PostMapping("/trade/chat")
    public String chat(HttpSession session) {
        Long postId = (Long) session.getAttribute("postId");
        tradeService.increaseChatInSession(session);

        if (postId == null) {
            return "redirect:/trade";  // ID 없으면 리스트 페이지 등으로 이동
        }

        return "redirect:/trade/post/" + postId;  // ID 포함 이동
    }
}