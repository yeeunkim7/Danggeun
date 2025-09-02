package org.example.danggeun.chat.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;

import java.time.LocalDateTime;
import java.util.Map;

@Controller
@RequiredArgsConstructor
@Slf4j
public class ChatMessageController {

    private final SimpMessagingTemplate messagingTemplate;

    @MessageMapping("/chat")
    public void handleChatMessage(@Payload Map<String, Object> message) {
        try {
            log.info("채팅 메시지 수신: {}", message);

            // chatRoomId도 안전하게 처리
            Object chatRoomIdObj = message.get("chatRoomId");
            String chatRoomId = chatRoomIdObj != null ? chatRoomIdObj.toString() : null;

            String content = (String) message.get("content");
            Object senderIdObj = message.get("senderId");
            String senderId = senderIdObj != null ? senderIdObj.toString() : "unknown";

            log.info("파싱된 데이터 - chatRoomId: {}, senderId: {}, content: {}", chatRoomId, senderId, content);

            if (chatRoomId == null || content == null) {
                log.error("필수 필드가 누락됨: chatRoomId={}, content={}", chatRoomId, content);
                return;
            }

            // 메시지를 해당 채팅방 구독자들에게 전송
            Map<String, Object> responseMessage = Map.of(
                    "content", content,
                    "senderId", senderId,
                    "timestamp", LocalDateTime.now().toString(),
                    "chatRoomId", chatRoomId,
                    "type", "CHAT"
            );

            messagingTemplate.convertAndSend("/topic/chat/" + chatRoomId, responseMessage);
            log.info("메시지 브로드캐스트 완료: /topic/chat/{}", chatRoomId);

            // AI 봇 응답 (봇과의 채팅방인 경우)
            if (shouldSendBotResponse(chatRoomId, senderId)) {
                log.info("봇 응답 조건 만족 - 봇 응답 전송 시작");
                sendBotResponse(chatRoomId, content);
            } else {
                log.info("봇 응답 조건 불만족 - chatRoomId: {}, senderId: {}", chatRoomId, senderId);
            }

        } catch (Exception e) {
            log.error("채팅 메시지 처리 중 오류 발생", e);
        }
    }

    private boolean shouldSendBotResponse(String chatRoomId, String senderId) {
        // 봇이 아닌 사용자가 보낸 메시지에만 응답 (타입 안전한 비교)
        boolean isBot = "bot".equals(senderId) || "999".equals(senderId);
        log.info("봇 응답 조건 확인 - senderId: {}, isBot: {}", senderId, isBot);
        return !isBot;
    }

    private void sendBotResponse(String chatRoomId, String userMessage) {
        // 별도 스레드에서 봇 응답 처리
        new Thread(() -> {
            try {
                log.info("봇 응답 생성 시작 - 사용자 메시지: {}", userMessage);

                // 1초 대기 (타이핑 시뮬레이션)
                Thread.sleep(1000);

                // 간단한 AI 응답 생성
                String botResponse = generateBotResponse(userMessage);
                log.info("봇 응답 생성 완료: {}", botResponse);

                Map<String, Object> botMessage = Map.of(
                        "content", botResponse,
                        "senderId", "999", // 봇 ID
                        "timestamp", LocalDateTime.now().toString(),
                        "chatRoomId", chatRoomId,
                        "type", "CHAT"
                );

                messagingTemplate.convertAndSend("/topic/chat/" + chatRoomId, botMessage);
                log.info("봇 응답 전송 완료 - 채팅방: {}, 내용: {}", chatRoomId, botResponse);

            } catch (InterruptedException e) {
                log.error("봇 응답 전송 중 인터럽트 발생", e);
                Thread.currentThread().interrupt();
            } catch (Exception e) {
                log.error("봇 응답 전송 중 오류 발생", e);
            }
        }).start();
    }

    private String generateBotResponse(String userMessage) {
        // 간단한 키워드 기반 응답
        String message = userMessage.toLowerCase();

        if (message.contains("안녕") || message.contains("hi") || message.contains("hello")) {
            return "안녕하세요! 당근마켓 AI 도우미입니다. 무엇을 도와드릴까요?";
        } else if (message.contains("거래") || message.contains("판매") || message.contains("구매")) {
            return "거래 관련 문의시네요. 어떤 물품을 찾고 계신가요?";
        } else if (message.contains("노트북")) {
            return "노트북 거래 문의시군요! 어떤 브랜드나 사양을 원하시나요?";
        } else if (message.contains("가격") || message.contains("얼마")) {
            return "가격 문의주셨네요. 구체적인 상품명을 알려주시면 더 정확한 정보를 드릴 수 있어요.";
        } else if (message.contains("감사") || message.contains("고마워")) {
            return "천만에요! 더 궁금한 것이 있으시면 언제든 물어보세요.";
        } else if (message.contains("김영수")) {
            return "김영수님에 대한 문의시군요. 더 구체적으로 어떤 정보가 필요하신가요?";
        } else {
            return "'" + userMessage + "'에 대해 문의주셨네요. 더 구체적으로 설명해주시면 도움을 드릴 수 있을 것 같아요!";
        }
    }
}