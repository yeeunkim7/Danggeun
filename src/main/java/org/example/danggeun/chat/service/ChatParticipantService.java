package org.example.danggeun.chat.service;

import lombok.RequiredArgsConstructor;
import org.example.danggeun.chat.repository.ChatParticipantRepository;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

@RequiredArgsConstructor
public class ChatParticipantService {

    private final ChatParticipantRepository chatParticipantRepository;

    public Set<String> getUserIdUnChatRoom(Long chatRoomId) {
        List<String> userIdList = chatParticipantRepository.findUserIdsByChatRoomId(chatRoomId);
        return new HashSet<>(userIdList);
    }
}
