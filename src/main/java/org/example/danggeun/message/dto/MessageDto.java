package org.example.danggeun.message.dto;

import lombok.*;
import org.example.danggeun.message.entity.Message;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MessageDto {
    private Long id;
    private Long senderId;
    private String senderName;
    private String content;
    private LocalDateTime createdAt;

    // Entity → Dto 변환 메서드 (정적 팩토리)
    public static MessageDto from(Message message) {
        return MessageDto.builder()
                .id(message.getId())
                .senderId(message.getSender().getId())
                .senderName(message.getSender().getUsername())
                .content(message.getContent())
                .createdAt(message.getCreatedAt())
                .build();
    }
}
