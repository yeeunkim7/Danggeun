package org.example.danggeun.chat.dto;

import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ChatMessageDto {
    private Long chatId;
    private Long senderId;
    private Long receiverId;
    private String content;

}
