package org.example.danggeun.chat.websocket;

import org.springframework.web.socket.WebSocketSession;

public class WebSocketUtils {

    public static String getUserIdFromSession(WebSocketSession session) {
        if (session.getPrincipal() != null) {
            return session.getPrincipal().getName();
        }
        Object userIdAttr = session.getAttributes().get("userId");
        return userIdAttr != null ? userIdAttr.toString() : null;
    }
}
