package org.example.danggeun.chat.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.danggeun.chat.dto.ChatMessageDto;
import org.example.danggeun.chat.entity.Chat;
import org.example.danggeun.chat.service.ChatService;
import org.example.danggeun.message.service.MessageService;
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
import org.springframework.web.bind.annotation.GetMapping;

import java.security.Principal;

@Slf4j
@Controller
@RequiredArgsConstructor
public class ChatController {

    private final ChatService chatService;
    private final MessageService messageService;
    private final SimpMessagingTemplate messagingTemplate;
    private final UserService userService;

    @MessageMapping("/chat.send")
    public void onMessageReceived(@Payload ChatMessageDto dto) {
        messageService.saveMessage(dto.getChatId(), dto.getUserId(), dto.getContent());
        String aiReply = chatService.getAiResponse(dto.getContent());
        Long botId = chatService.getAiUserId();
        messageService.saveMessage(dto.getChatId(), botId, aiReply);
        ChatMessageDto botDto = ChatMessageDto.builder()
                .chatId(dto.getChatId())
                .userId(botId)
                .content(aiReply)
                .type(ChatMessageDto.MessageType.BOT)
                .build();
        messagingTemplate.convertAndSend("/topic/public/" + dto.getChatId(), botDto);
    }

    @GetMapping("/chat")
    public String chatPage(
            Authentication authentication,
            Model model
    ) {
        // principal 타입 검사해서 이메일 꺼내기
        Object principal = authentication.getPrincipal();
        String loginEmail;
        if (principal instanceof OAuth2User oauth2User) {
            loginEmail = oauth2User.getAttribute("email");
        } else if (principal instanceof UserDetails userDetails) {
            loginEmail = userDetails.getUsername();
        } else {
            throw new IllegalStateException("알 수 없는 인증 타입: " + principal.getClass());
        }

        // 유저 엔티티 조회
        User loginUser = userService.findByEmail(loginEmail)
                .orElseThrow(() -> new IllegalStateException("존재하지 않는 사용자: " + loginEmail));

        // AI 챗방 조회 또는 생성
        Chat aiChat = chatService.findOrCreateAiChat(loginUser.getId());

        model.addAttribute("chatId", aiChat.getId());
        model.addAttribute("loginEmail", loginEmail);
        return "chat/chat";
    }
}
