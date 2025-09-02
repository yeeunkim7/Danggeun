package org.example.danggeun.chat.websocket.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ChatSocketMessageDto {
    private String messageId;
    private Long chatRoomId;
    private Long senderId;
    private String senderName;
    private String content;
    private LocalDateTime timestamp;
    private MessageType type;

    public enum MessageType {
        CHAT,      // 일반 채팅 메시지
        JOIN,      // 입장 메시지
        LEAVE,     // 퇴장 메시지
        SYSTEM     // 시스템 메시지
    }

    // 기본값 설정
    public ChatSocketMessageDto(Long chatRoomId, Long senderId, String content) {
        this.chatRoomId = chatRoomId;
        this.senderId = senderId;
        this.content = content;
        this.timestamp = LocalDateTime.now();
        this.type = MessageType.CHAT;
    }
}