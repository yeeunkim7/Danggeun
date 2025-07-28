package org.example.danggeun.message.repository;

import org.example.danggeun.message.entity.Message;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Repository
public interface MessageRepository extends JpaRepository<Message, Long> {

    List<Message> findAllByChatIdOrderByCreatedAtAsc(Long chatId);

    @Transactional
    @Modifying
    @Query("UPDATE Message m SET m.read = true WHERE m.chatRoom.id = :chatRoomId AND m.receiver.id = :userId")
    int markMessagesAsRead(@Param("chatRoomId") Long chatRoomId, @Param("userId") String userId);

    List<Message> findByChatRoomIdOrderByTimestampAsc(Long chatRoomId);
}
