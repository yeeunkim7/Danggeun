package org.example.danggeun.chat.websocket.dto;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class ChatSocketMessageDto {

    public enum MessageType {
        CHAT, JOIN, LEAVE, READ
    }

    private MessageType type;
    private String messageId;
    private Long chatRoomId;
    private Long senderId;
    private String content;
    private LocalDateTime timestamp;
}
