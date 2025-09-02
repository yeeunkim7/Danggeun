package org.example.danggeun.message.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.example.danggeun.message.entity.Message;

import java.time.LocalDateTime;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class MessageDto {
    private Long messageId;
    private Long chatId;
    private Long senderId;
    private String senderName;
    private String content;
    private LocalDateTime createdAt;
    private boolean isRead;

    public static MessageDto from(Message message) {
        return MessageDto.builder()
                .messageId(message.getId())
                .chatId(message.getChat().getId())
                .senderId(message.getSender().getId())
                .senderName(message.getSender().getUsername())
                .content(message.getContent())
                .createdAt(message.getCreatedAt())
                .isRead(message.isRead())
                .build();
    }
}