package org.example.danggeun.message.service;

import lombok.RequiredArgsConstructor;
import org.example.danggeun.chat.entity.Chat;
import org.example.danggeun.chat.repository.ChatRepository;
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
public class MessageService {

    private final MessageRepository messageRepository;
    private final ChatRepository chatRepository;
    private final UserRepository userRepository;

    @Transactional
    public Message saveMessage(Long chatId, Long senderId, Long receiverId, String content) {
        Chat chat = chatRepository.findById(chatId)
                .orElseThrow(() -> new IllegalArgumentException("Invalid chatId"));
        User sender = userRepository.findById(senderId)
                .orElseThrow(() -> new IllegalArgumentException("Invalid senderId"));
        // receiverId는 필요하면 사용

        Message msg = Message.builder()
                .chat(chat)
                .sender(sender)
                .content(content)
                .createdAt(LocalDateTime.now())
                .build();

        return messageRepository.save(msg);
    }

    @Transactional(readOnly = true)
    public List<Message> getMessages(Long chatId) {
        return messageRepository.findAllByChatIdOrderByCreatedAtAsc(chatId);
    }

    public List<Chat> findAllChatsByUser(User user) {
        return chatRepository.findAllByBuyerOrSeller(user, user);
    }
}
