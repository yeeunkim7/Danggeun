package org.example.danggeun.chat.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.danggeun.chat.entity.Chat;
import org.example.danggeun.chat.repository.ChatRepository;
import org.example.danggeun.chat.websocket.dto.ChatSocketMessageDto;
import org.example.danggeun.message.entity.Message;
import org.example.danggeun.message.repository.MessageRepository;
import org.example.danggeun.user.entity.User;
import org.example.danggeun.user.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class ChatSocketMessageService {

    private final MessageRepository messageRepository;
    private final ChatRepository chatRepository;
    private final UserRepository userRepository;

    /**
     * WebSocket으로 수신한 채팅 메시지를 DB에 저장
     */
    @Transactional
    public Message saveMessage(ChatSocketMessageDto dto) {
        try {
            Chat chat = chatRepository.findById(dto.getChatRoomId())
                    .orElseThrow(() -> new IllegalArgumentException("Invalid chatRoomId: " + dto.getChatRoomId()));

            User sender = userRepository.findById(dto.getSenderId())
                    .orElseThrow(() -> new IllegalArgumentException("Invalid senderId: " + dto.getSenderId()));

            Message message = Message.builder()
                    .chat(chat)
                    .sender(sender)
                    .content(dto.getContent())
                    .createdAt(dto.getTimestamp() != null ? dto.getTimestamp() : LocalDateTime.now())
                    .isRead(false)  // 기본값으로 읽지 않음 상태
                    .build();

            Message savedMessage = messageRepository.save(message);
            log.info("메시지 저장 성공: messageId={}, chatRoomId={}, senderId={}",
                    savedMessage.getId(), dto.getChatRoomId(), dto.getSenderId());

            return savedMessage;

        } catch (Exception e) {
            log.error("메시지 저장 중 오류 발생: chatRoomId={}, senderId={}",
                    dto.getChatRoomId(), dto.getSenderId(), e);
            throw e;
        }
    }

    /**
     * 해당 채팅방에서 유저가 읽지 않은 메시지를 읽음 처리
     */
    @Transactional
    public int markMessagesAsRead(Long chatRoomId, Long userId) {
        try {
            int updatedCount = messageRepository.markMessagesAsRead(chatRoomId, userId);
            log.info("{}개의 메시지를 읽음 처리 (chatRoomId={}, userId={})", updatedCount, chatRoomId, userId);
            return updatedCount;
        } catch (Exception e) {
            log.error("메시지 읽음 처리 중 오류 발생: chatRoomId={}, userId={}", chatRoomId, userId, e);
            return 0;
        }
    }

    /**
     * 특정 채팅방의 모든 메시지 조회 (시간순)
     */
    @Transactional(readOnly = true)
    public List<Message> getMessages(Long chatRoomId) {
        try {
            return messageRepository.findByChatIdOrderByCreatedAtAsc(chatRoomId);
        } catch (Exception e) {
            log.error("메시지 조회 중 오류 발생: chatRoomId={}", chatRoomId, e);
            throw new RuntimeException("메시지를 조회할 수 없습니다: " + chatRoomId);
        }
    }
}