package org.example.danggeun.chat.dto;

import lombok.*;
import java.time.LocalDateTime;

/**
 * 채팅방 정보를 전달하기 위한 DTO
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ChatDto {

    private Long id;

    private LocalDateTime createdAt;

    private Long buyerId;

    private Long sellerId;

    private Long productId;
}
