package org.example.danggeun.chat.websocket.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class ConnectionMessage {
    private String type = "CONNECTED";
    private String message;
}
