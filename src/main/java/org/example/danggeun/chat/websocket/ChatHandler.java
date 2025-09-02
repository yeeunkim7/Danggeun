/*package org.example.danggeun.chat.websocket;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.danggeun.chat.service.ChatSocketMessageService;
import org.example.danggeun.chat.websocket.dto.ChatSocketMessageDto;
import org.example.danggeun.chat.websocket.dto.ReadReceipt;
import org.example.danggeun.message.entity.Message;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.*;

import org.springframework.web.socket.handler.TextWebSocketHandler;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

@Component
@RequiredArgsConstructor
@Slf4j
public class ChatHandler extends TextWebSocketHandler {

    private final Map<String, WebSocketSession> sessions = new ConcurrentHashMap<>();
    private final Map<String, String> sessionUserMap = new ConcurrentHashMap<>();
    private final ChatSocketMessageService messageService;
    private final ObjectMapper objectMapper;

    @Override
    public void afterConnectionEstablished(WebSocketSession session) throws Exception {
        String sessionId = session.getId();
        sessions.put(sessionId, session);

        String userId = getUserIdFromSession(session);
        if (userId != null) {
            sessionUserMap.put(sessionId, userId);
            log.info("WebSocket connected - sessionId: {}, userId: {}", sessionId, userId);

            sendMessage(session, Map.of(
                    "type", "CONNECTED",
                    "message", "채팅 서버에 연결되었습니다."
            ));
        } else {
            session.close(CloseStatus.NOT_ACCEPTABLE.withReason("인증되지 않은 사용자"));
        }
    }

    @Override
    protected void handleTextMessage(WebSocketSession session, TextMessage message) throws Exception {
        try {
            String payload = message.getPayload();
            ChatSocketMessageDto chatSocketMessageDto = objectMapper.readValue(payload, ChatSocketMessageDto.class);

            switch (chatSocketMessageDto.getType()) {
                case CHAT -> handleChatMessage(session, chatSocketMessageDto);
                case JOIN -> handleJoinMessage(session, chatSocketMessageDto);
                case LEAVE -> handleLeaveMessage(session, chatSocketMessageDto);
                case READ -> handleReadMessage(session, chatSocketMessageDto);
                default -> log.warn("Unknown message type: {}", chatSocketMessageDto.getType());
            }

        } catch (Exception e) {
            log.error("Error handling message", e);
            sendError(session, "메시지 처리 중 오류가 발생했습니다.");
        }
    }

    private void handleChatMessage(WebSocketSession session, ChatSocketMessageDto chatSocketMessageDto) {
        String userId = sessionUserMap.get(session.getId());

        if (chatSocketMessageDto.getMessageId() == null) {
            chatSocketMessageDto.setMessageId(UUID.randomUUID().toString());
        }

        chatSocketMessageDto.setTimestamp(LocalDateTime.now());
        chatSocketMessageDto.setSenderId(Long.valueOf(userId));

        Message savedMessage = messageService.saveMessage(chatSocketMessageDto);

        Set<String> participants = getChatRoomParticipants(chatSocketMessageDto.getChatRoomId());

        for (String participantId : participants) {
            WebSocketSession participantSession = getSessionByUserId(participantId);
            if (participantSession != null && participantSession.isOpen()) {
                try {
                    sendMessage(participantSession, savedMessage);
                } catch (Exception e) {
                    log.error("Failed to send message to participant: {}", participantId, e);
                }
            }
        }

        notifyOfflineUsers(chatSocketMessageDto, participants);
    }

    private void handleReadMessage(WebSocketSession session, ChatSocketMessageDto readMessage) {
        String userId = sessionUserMap.get(session.getId());
        Long chatRoomId = readMessage.getChatRoomId();

        int updatedCount = messageService.markMessagesAsRead(chatRoomId, userId);

        ReadReceipt receipt = new ReadReceipt(chatRoomId, userId, LocalDateTime.now(), updatedCount);

        Set<String> participants = getChatRoomParticipants(chatRoomId);
        for (String participantId : participants) {
            if (!participantId.equals(userId)) {
                WebSocketSession participantSession = getSessionByUserId(participantId);
                if (participantSession != null && participantSession.isOpen()) {
                    try {
                        sendMessage(participantSession, receipt);
                    } catch (Exception e) {
                        log.error("Failed to send read receipt to: {}", participantId, e);
                    }
                }
            }
        }
    }

    private void handleJoinMessage(WebSocketSession session, ChatSocketMessageDto joinMessage) {
        log.info("JOIN message received from user: {}", sessionUserMap.get(session.getId()));
    }

    private void handleLeaveMessage(WebSocketSession session, ChatSocketMessageDto leaveMessage) {
        log.info("LEAVE message received from user: {}", sessionUserMap.get(session.getId()));
    }

    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) throws Exception {
        String sessionId = session.getId();
        String userId = sessionUserMap.remove(sessionId);
        sessions.remove(sessionId);

        log.info("WebSocket disconnected - sessionId: {}, userId: {}, status: {}", sessionId, userId, status);

        if (userId != null) {
            notifyUserDisconnection(userId);
        }
    }

    @Override
    public void handleTransportError(WebSocketSession session, Throwable exception) throws Exception {
        log.error("WebSocket transport error - sessionId: {}", session.getId(), exception);
        session.close(CloseStatus.SERVER_ERROR);
    }

    @Scheduled(fixedDelay = 30000)
    public void sendHeartbeat() {
        sessions.values().parallelStream().forEach(session -> {
            if (session.isOpen()) {
                try {
                    session.sendMessage(new TextMessage("{\"type\":\"HEARTBEAT\"}"));
                } catch (Exception e) {
                    log.error("Failed to send heartbeat to session: {}", session.getId());
                }
            }
        });
    }

    private void sendMessage(WebSocketSession session, Object message) throws IOException {
        if (session.isOpen()) {
            String json = objectMapper.writeValueAsString(message);
            session.sendMessage(new TextMessage(json));
        }
    }

    private void sendError(WebSocketSession session, String errorMessage) {
        try {
            sendMessage(session, Map.of(
                    "type", "ERROR",
                    "message", errorMessage,
                    "timestamp", LocalDateTime.now().toString()
            ));
        } catch (Exception e) {
            log.error("Failed to send error message", e);
        }
    }

    // 이하 헬퍼 메서드는 실제 구현 필요
    private String getUserIdFromSession(WebSocketSession session) {
        return session.getPrincipal() != null ? session.getPrincipal().getName() : null;
    }

    private Set<String> getChatRoomParticipants(Long chatRoomId) {
        return new HashSet<>(); // 실제 구현 필요
    }

    private WebSocketSession getSessionByUserId(String userId) {
        return sessions.entrySet().stream()
                .filter(e -> userId.equals(sessionUserMap.get(e.getKey())))
                .map(Map.Entry::getValue)
                .findFirst()
                .orElse(null);
    }

    private void notifyUserDisconnection(String userId) {
        log.info("User {} disconnected, broadcasting LEAVE", userId);
        // 실제 로직 필요 시 구현
    }

    private void notifyOfflineUsers(ChatSocketMessageDto msg, Set<String> participants) {
        log.info("오프라인 사용자에게 알림 전송 (예시)"); // 실제 구현 필요
    }
} */
