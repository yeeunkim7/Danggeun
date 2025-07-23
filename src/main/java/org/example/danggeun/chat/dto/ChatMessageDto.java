package org.example.danggeun.chat.dto;

import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ChatMessageDto {
    private Long chatId;
    private Long senderId;   // 보내는 사람
    private Long receiverId; // 받는 사람
    private String content;

}
