package org.example.danggeun.chat.service;

import com.google.genai.Client;
import com.google.genai.types.GenerateContentConfig;
import com.google.genai.types.GenerateContentResponse;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.danggeun.chat.dto.ChatSummaryDto;
import org.example.danggeun.chat.entity.Chat;
import org.example.danggeun.chat.repository.ChatRepository;
import org.example.danggeun.message.entity.Message;
import org.example.danggeun.trade.entity.Trade;
import org.example.danggeun.user.entity.User;
import org.example.danggeun.user.service.UserService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class ChatService {

    private static final String AI_EMAIL = "system@danggeun.com";

    private final UserService userService;
    private final ChatRepository chatRepository;

    @Value("${GEMINI_API_KEY:}")
    private String apiKey;

    private Client geminiClient;
    private GenerateContentConfig generationConfig;

    @PostConstruct
    public void init() {
        try {
            if (apiKey == null || apiKey.isEmpty()) {
                log.warn("GEMINI_API_KEY가 설정되지 않았습니다. AI 채팅 기능이 제한됩니다.");
                return;
            }

            this.generationConfig = GenerateContentConfig.builder()
                    .temperature(0.7f)
                    .topP(1.0f)
                    .build();

            this.geminiClient = Client.builder()
                    .apiKey(this.apiKey)
                    .build();

            log.info("Gemini Client 초기화 성공!");
        } catch (Exception e) {
            log.error("Gemini Client 초기화 실패", e);
        }
    }

    public String getAiResponse(String userMessage) {
        if (geminiClient == null) {
            return "죄송합니다. AI 서비스가 현재 사용할 수 없습니다.";
        }

        try {
            GenerateContentResponse response = this.geminiClient.models.generateContent(
                    "gemini-1.5-flash-latest",
                    "당근마켓 중고거래 도우미로서 친근하게 답변해주세요: " + userMessage,
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

        // AI 채팅은 product가 null인 채팅으로 구분
        return chatRepository
                .findByBuyerAndSellerAndProductIsNull(buyer, aiUser)
                .orElseGet(() -> {
                    Chat chat = Chat.builder()
                            .buyer(buyer)
                            .seller(aiUser)
                            .product(null)  // AI 채팅은 제품 없음
                            .build();
                    return chatRepository.save(chat);
                });
    }

    @Transactional
    protected User findOrCreateAiUser() {
        return userService.findByEmail(AI_EMAIL).orElseGet(() -> {
            User system = User.builder()
                    .username("AI 도우미")
                    .email(AI_EMAIL)
                    .password("{noop}changeme")
                    .build();
            return userService.save(system);
        });
    }

    @Transactional
    public Chat findOrCreateTradeChat(User buyer, User seller, Trade trade) {
        return chatRepository.findByBuyerAndSellerAndProduct(buyer, seller, trade)
                .orElseGet(() -> chatRepository.save(
                        Chat.builder()
                                .buyer(buyer)
                                .seller(seller)
                                .product(trade)
                                .build()
                ));
    }

    @Transactional(readOnly = true)
    public List<ChatSummaryDto> findAllChatsOfUser(Long userId) {
        User user = userService.findById(userId);
        List<Chat> chats = chatRepository.findAllByBuyerOrSeller(user, user);

        List<ChatSummaryDto> result = new ArrayList<>();
        for (Chat chat : chats) {
            Message lastMsg = chat.getMessages().isEmpty() ? null
                    : chat.getMessages().get(chat.getMessages().size() - 1);
            String lastMessage = (lastMsg != null ? lastMsg.getContent() : "");

            Long buyerId = chat.getBuyer().getId();
            Long sellerId = chat.getSeller().getId();
            Long opponentId = buyerId.equals(userId) ? sellerId : buyerId;

            User otherUser = userId.equals(chat.getBuyer().getId())
                    ? chat.getSeller()
                    : chat.getBuyer();

            String title;
            if (chat.getProduct() != null) {
                title = chat.getProduct().getTitle();
            } else {
                // AI 채팅인 경우
                if (AI_EMAIL.equals(otherUser.getEmail())) {
                    title = "AI 도우미";
                } else {
                    title = otherUser.getUsername();
                }
            }

            result.add(ChatSummaryDto.builder()
                    .chatId(chat.getId())
                    .title(title)
                    .lastMessage(lastMessage)
                    .buyerId(buyerId)
                    .sellerId(sellerId)
                    .opponentId(opponentId)
                    .build()
            );
        }
        return result;
    }

    @Transactional(readOnly = true)
    public Chat findById(Long chatId) {
        return chatRepository.findById(chatId)
                .orElseThrow(() -> new IllegalArgumentException("채팅방을 찾을 수 없습니다: " + chatId));
    }
}