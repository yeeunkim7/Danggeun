package org.example.danggeun.chat.repository;

import org.example.danggeun.chat.entity.ChatParticipant;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ChatParticipantRepository extends CrudRepository<ChatParticipant, Long> {

    @Query("SELECT cp.userId FROM ChatParticipant cp WHERE cp.chatRoomId = :chatRoomId")
    List<String> findUserIdsByChatRoomId(Long chatRoomId);

}
