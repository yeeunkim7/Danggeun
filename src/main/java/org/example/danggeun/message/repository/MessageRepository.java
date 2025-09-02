package org.example.danggeun.message.repository;

import org.example.danggeun.message.entity.Message;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MessageRepository extends JpaRepository<Message, Long> {

    /**
     * 특정 채팅방의 모든 메시지를 시간순으로 조회
     */
    List<Message> findByChatIdOrderByCreatedAtAsc(Long chatId);

    /**
     * 채팅방에서 특정 사용자가 보내지 않은 메시지들을 읽음 처리
     * (자신이 보낸 메시지는 읽음 처리하지 않음)
     */
    @Modifying
    @Query("UPDATE Message m SET m.isRead = true WHERE m.chat.id = :chatRoomId AND m.sender.id != :userId AND m.isRead = false")
    int markMessagesAsRead(@Param("chatRoomId") Long chatRoomId, @Param("userId") Long userId);

    /**
     * 특정 채팅방에서 읽지 않은 메시지 개수 조회
     */
    @Query("SELECT COUNT(m) FROM Message m WHERE m.chat.id = :chatRoomId AND m.sender.id != :userId AND m.isRead = false")
    long countUnreadMessages(@Param("chatRoomId") Long chatRoomId, @Param("userId") Long userId);

    /**
     * 특정 채팅방의 마지막 메시지 조회
     */
    @Query("SELECT m FROM Message m WHERE m.chat.id = :chatId ORDER BY m.createdAt DESC LIMIT 1")
    Message findLastMessageByChatId(@Param("chatId") Long chatId);
}