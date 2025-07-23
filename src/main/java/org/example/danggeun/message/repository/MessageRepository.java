package org.example.danggeun.message.repository;

import org.example.danggeun.message.entity.Message;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MessageRepository extends JpaRepository<Message, Long> {

    List<Message> findAllByChatIdOrderByCreatedAtAsc(Long chatId);
}
