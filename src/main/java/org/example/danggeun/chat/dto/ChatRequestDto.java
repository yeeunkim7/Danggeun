package org.example.danggeun.chat.dto;

import lombok.*;
import java.time.LocalDateTime;


@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ChatRequestDto {

    private Long id;

    private LocalDateTime createdAt;

    private Long buyerId;

    private Long sellerId;

    private Long productId;
}
