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
    public String chatPage(Principal principal, Model model) {
        User loginUser = userService.findByEmail(principal.getName())
                .orElseThrow(() -> new IllegalStateException("존재하지 않는 사용자: " + principal.getName()));
        Chat aiChat = chatService.findOrCreateAiChat(loginUser.getId());
        model.addAttribute("chatId", aiChat.getId());
        model.addAttribute("loginUser", loginUser);
        return "chat/chat";
    }
}
