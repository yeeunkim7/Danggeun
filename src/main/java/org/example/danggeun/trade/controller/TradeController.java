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
    public String showPostDetail(@PathVariable Long id, Model model, HttpSession session) {
        Write post = writeRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("게시글 없음"));

        Long userId = (Long) session.getAttribute("userId");

        //  조회수 증가: 세션 키 기반으로 중복 방지
        String viewKey = "viewed_" + id + "_" + userId;
        if (session.getAttribute(viewKey) == null) {
            post.setViews(post.getViews() + 1);
            writeRepository.save(post);
            session.setAttribute(viewKey, true); // 이미 봤다고 기록
        }

        model.addAttribute("dto", post);
        model.addAttribute("sessionUserId", userId);
        return "trade/tradePost";
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
        Long userId = (Long) session.getAttribute("userId");

        if (postId == null || userId == null) return "redirect:/trade";

        //채팅 카운트 증가: 세션 키 기반 중복 방지
        String chatKey = "chatted_" + postId + "_" + userId;
        if (session.getAttribute(chatKey) == null) {
            Write post = writeRepository.findById(postId)
                    .orElseThrow(() -> new RuntimeException("게시글 없음"));
            post.setChats(post.getChats() + 1);
            writeRepository.save(post);
            session.setAttribute(chatKey, true); // 이미 채팅했다는 표시
        }

        return "redirect:/trade/post/" + postId;
    }

    // 게시글 삭제
    @PostMapping("/trade/delete/{id}")
    public String deletePost(@PathVariable Long id, HttpSession session) {
        Write post = writeRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("해당 게시글을 찾을 수 없습니다."));

        writeRepository.deleteById(id);
        return "redirect:/trade";
    }

    @GetMapping("/trade/edit/{id}")
    public String showEditPage(@PathVariable Long id, Model model) {
        Write post = writeRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("게시글 없음"));
        model.addAttribute("dto", post);
        return "trade/updateTradePost";
    }

    @PostMapping("/trade/edit/{id}")
    public String updatePost(
            @PathVariable Long id,
            @ModelAttribute TradeDto tradeDto,
            @RequestParam(value = "productImage", required = false) MultipartFile file
    ) {
        tradeService.updatePost(id, tradeDto, file);
        return "redirect:/trade/post/" + id;
    }
}