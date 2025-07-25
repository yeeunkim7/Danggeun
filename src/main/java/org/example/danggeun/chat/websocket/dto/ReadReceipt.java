package org.example.danggeun.chat.websocket.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@AllArgsConstructor
public class ReadReceipt {
    private Long chatRoomId;
    private String readerId;
    private LocalDateTime readAt;
    private int readCount;
}
