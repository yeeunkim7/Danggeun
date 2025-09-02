package org.example.danggeun.chat.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.danggeun.security.CustomUserDetails;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
@RequiredArgsConstructor
@Slf4j
public class ChatPageController {

    @GetMapping("/chat")
    public String chatPage(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @RequestParam(value = "chatId", required = false) Long chatId,
            Model model) {

        log.info("채팅 페이지 접근 - 사용자: {}, 채팅방 ID: {}",
                userDetails != null ? userDetails.getUsername() : "익명", chatId);

        if (userDetails == null) {
            log.warn("인증되지 않은 사용자가 채팅 페이지에 접근하려고 함");
            return "redirect:/login";
        }

        // 임시 데이터 - 실제로는 데이터베이스에서 조회
        List<Map<String, Object>> chatRooms = createSampleChatRooms();

        // 선택된 채팅방 ID (없으면 첫 번째 채팅방)
        Long selectedChatId = chatId != null ? chatId :
                (!chatRooms.isEmpty() ? (Long) chatRooms.get(0).get("chatId") : 1L);

        // 봇 ID (AI 봇과의 채팅을 위함)
        Long botId = 999L; // 임시 봇 ID

        model.addAttribute("loginUser", Map.of(
                "id", userDetails.getUser().getId(), // User 엔티티에서 ID 가져오기
                "username", userDetails.getUsername()
        ));
        model.addAttribute("chatRooms", chatRooms);
        model.addAttribute("selectedChatId", selectedChatId);
        model.addAttribute("botId", botId);

        log.info("채팅 페이지 모델 설정 완료 - 채팅방 수: {}, 선택된 채팅방: {}",
                chatRooms.size(), selectedChatId);

        return "chat/chat";
    }

    private List<Map<String, Object>> createSampleChatRooms() {
        List<Map<String, Object>> chatRooms = new ArrayList<>();

        // AI 봇과의 채팅방
        Map<String, Object> botRoom = new HashMap<>();
        botRoom.put("chatId", 1L);
        botRoom.put("title", "AI 도우미");
        botRoom.put("lastMessage", "안녕하세요! 무엇을 도와드릴까요?");
        botRoom.put("opponentId", 999L); // 봇 ID
        chatRooms.add(botRoom);

        // 추가 샘플 채팅방들 (필요시)
        Map<String, Object> sampleRoom = new HashMap<>();
        sampleRoom.put("chatId", 2L);
        sampleRoom.put("title", "김철수");
        sampleRoom.put("lastMessage", "노트북 거래 문의드립니다.");
        sampleRoom.put("opponentId", 100L);
        chatRooms.add(sampleRoom);

        return chatRooms;
    }
}