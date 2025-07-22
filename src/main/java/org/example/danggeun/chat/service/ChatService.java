package org.example.danggeun.chat.service;

import com.google.genai.Client;
import com.google.genai.types.GenerateContentConfig;
import com.google.genai.types.GenerateContentResponse;
import jakarta.annotation.PostConstruct;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.danggeun.chat.entity.Chat;
import org.example.danggeun.chat.repository.ChatRepository;
import org.example.danggeun.user.entity.User;
import org.example.danggeun.user.service.UserService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class ChatService {

    // AI 챗봇 시스템 계정 이메일
    private static final String AI_EMAIL = "system@danggeun.com";

    private final UserService userService;
    private final ChatRepository chatRepository;

    @Value("${GEMINI_API_KEY}")
    private String apiKey;

    private Client geminiClient;
    private GenerateContentConfig generationConfig;

    @PostConstruct
    public void init() {
        this.generationConfig = GenerateContentConfig.builder()
                .temperature(0.7f)
                .topP(1.0f)
                .build();

        this.geminiClient = Client.builder()
                .apiKey(this.apiKey)
                .build();

        log.info("Gemini Client 초기화 성공!");
    }

    public String getAiResponse(String userMessage) {
        try {
            GenerateContentResponse response = this.geminiClient.models.generateContent(
                    "gemini-1.5-flash-latest",
                    userMessage,
                    this.generationConfig
            );
            return response.text();
        } catch (Exception e) {
            log.error("Gemini API 호출 중 예상치 못한 오류가 발생했습니다.", e);
            return "죄송합니다, AI와 통신하는 중에 문제가 발생했습니다.";
        }
    }

    public Long getAiUserId() {
        return findOrCreateAiUser().getId();
    }

    @Transactional
    public Chat findOrCreateAiChat(Long loginUserId) {
        User buyer = userService.findById(loginUserId);
        User aiUser = findOrCreateAiUser();

        return chatRepository
                .findByBuyerAndSellerAndProductIsNull(buyer, aiUser)
                .orElseGet(() -> {
                    Chat chat = Chat.builder()
                            .buyer(buyer)
                            .seller(aiUser)
                            .product(null)
                            .build();
                    return chatRepository.save(chat);
                });
    }

    @Transactional
    protected User findOrCreateAiUser() {
        return userService.findByEmail(AI_EMAIL).orElseGet(() -> {
            User system = User.builder()
                    .username("chatbot")
                    .email(AI_EMAIL)
                    .password("{noop}changeme")  // 실제로는 랜덤·안전하게 처리
                    .build();
            return userService.save(system);
        });
    }
}
