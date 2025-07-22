package org.example.danggeun.chat.dto;

import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ChatMessageDto {
    private Long chatId;      // 채팅방 ID
    private Long userId;      // 보내는 사람(User PK)
    private String content;   // 메시지 내용
    private MessageType type; // USER or BOT

    public enum MessageType {
        USER, BOT
    }
}
