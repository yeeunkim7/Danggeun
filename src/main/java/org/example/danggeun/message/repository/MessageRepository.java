package org.example.danggeun.message.repository;

import org.example.danggeun.message.entity.Message;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MessageRepository extends JpaRepository<Message, Long> {
    // 특정 채팅방의 메시지 리스트(생성시간 순)
    List<Message> findAllByChatIdOrderByCreatedAtAsc(Long chatId);
}
