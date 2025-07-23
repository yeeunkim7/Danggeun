package org.example.danggeun.chat.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.danggeun.chat.dto.ChatMessageDto;
import org.example.danggeun.chat.dto.ChatRoomDto;
import org.example.danggeun.chat.dto.ChatSummaryDto;
import org.example.danggeun.chat.entity.Chat;
import org.example.danggeun.chat.service.ChatService;
import org.example.danggeun.message.dto.MessageDto;
import org.example.danggeun.message.service.MessageService;
import org.example.danggeun.trade.entity.Trade;
import org.example.danggeun.trade.service.TradeService;
import org.example.danggeun.user.entity.User;
import org.example.danggeun.user.service.UserService;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Controller
@RequiredArgsConstructor
public class ChatController {

    private final ChatService chatService;
    private final MessageService messageService;
    private final SimpMessagingTemplate messagingTemplate;
    private final UserService userService;
    private final TradeService tradeService;

    /** WebSocket 메시지 핸들러 */
    @MessageMapping("/chat.send")
    public void onMessageReceived(@Payload ChatMessageDto dto) {
        // 1) 메시지 저장
        messageService.saveMessage(dto.getChatId(), dto.getSenderId(), dto.getReceiverId(), dto.getContent());

        // 2) AI방이면 답변 생성·전송
        Chat chat = chatService.findById(dto.getChatId());
        if (chat.getProduct() == null) {
            String aiReply = chatService.getAiResponse(dto.getContent());
            Long botId    = chatService.getAiUserId();

            // 봇 답변 저장
            messageService.saveMessage(dto.getChatId(), botId, dto.getSenderId(), aiReply);

            ChatMessageDto botDto = ChatMessageDto.builder()
                    .chatId(dto.getChatId())
                    .senderId(botId)
                    .receiverId(dto.getSenderId())
                    .content(aiReply)
                    .build();

            messagingTemplate.convertAndSend("/topic/public/" + dto.getChatId(), botDto);
        }
    }

    /** 채팅 화면 공통 렌더링 */
    @GetMapping({"/chat", "/chat/room/detail/{chatId}"})
    public String chatRoom(
            @PathVariable(required = false) Long chatId,
            Authentication authentication,
            Model model
    ) {
        User loginUser = extractLoginUser(authentication);
        model.addAttribute("loginUser", loginUser);

        List<ChatSummaryDto> rooms = chatService.findAllChatsOfUser(loginUser.getId());
        model.addAttribute("chatRooms", rooms);

        if (chatId == null) {
            Chat aiChat = chatService.findOrCreateAiChat(loginUser.getId());
            chatId = aiChat.getId();
        }
        model.addAttribute("selectedChatId", chatId);

        model.addAttribute("botId", chatService.getAiUserId());

        return "chat/chat";
    }

    /** 게시글 기반 1:1 거래채팅 진입 (redirect to detail) */
    @GetMapping("/chat/room/{postId}")
    public String enterTradeChat(
            @PathVariable Long postId,
            Authentication authentication
    ) {
        User buyer = extractLoginUser(authentication);
        Trade trade = tradeService.findById(postId)
                .orElseThrow(() -> new IllegalStateException("상품 없음: " + postId));
        User seller = trade.getSeller();

        Chat chat = chatService.findOrCreateTradeChat(buyer, seller, trade);
        return "redirect:/chat/room/detail/" + chat.getId();
    }

    /** AJAX: 내 채팅방 리스트 JSON */
    @GetMapping("/api/chats")
    @ResponseBody
    public List<ChatSummaryDto> getMyChats(Authentication auth) {
        User user = extractLoginUser(auth);
        return chatService.findAllChatsOfUser(user.getId());
    }

    /** AJAX: 방별 메시지 리스트 JSON */
    @GetMapping("/api/chats/{chatId}/messages")
    @ResponseBody
    public List<MessageDto> getMessages(
            @PathVariable Long chatId, Authentication auth
    ) {
        // 권한 체크 생략
        return messageService.getMessages(chatId).stream()
                .map(MessageDto::from).toList();
    }

    /** 인증 객체에서 User 엔티티 반환 헬퍼 */
    private User extractLoginUser(Authentication authentication) {
        Object principal = authentication.getPrincipal();
        String email;
        if (principal instanceof OAuth2User o) {
            email = o.getAttribute("email");
        } else if (principal instanceof UserDetails ud) {
            email = ud.getUsername();
        } else {
            throw new IllegalStateException("알 수 없는 인증: " + principal.getClass());
        }
        return userService.findByEmail(email)
                .orElseThrow(() -> new IllegalStateException("사용자 없음: " + email));
    }


}
