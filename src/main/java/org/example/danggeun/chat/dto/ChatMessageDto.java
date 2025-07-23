package org.example.danggeun.chat.dto;

import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ChatMessageDto {
    private Long chatId;
    private Long userId;
    private String content;
    private MessageType type;

    public enum MessageType {
        USER, BOT
    }
}
