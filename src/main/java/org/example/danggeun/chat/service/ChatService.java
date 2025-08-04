package org.example.danggeun.chat.service;

import com.google.genai.Client;
import com.google.genai.types.GenerateContentConfig;
import com.google.genai.types.GenerateContentResponse;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.danggeun.category.entity.Category;
import org.example.danggeun.category.repository.CategoryRepository;
import org.example.danggeun.chat.dto.ChatSummaryDto;
import org.example.danggeun.chat.entity.Chat;
import org.example.danggeun.chat.repository.ChatRepository;
import org.example.danggeun.message.entity.Message;
import org.example.danggeun.trade.entity.Trade;
import org.example.danggeun.trade.repository.TradeRepository;
import org.example.danggeun.user.entity.User;
import org.example.danggeun.user.service.UserService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class ChatService {

    private final TradeRepository tradeRepository;
    private final CategoryRepository categoryRepository;
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
        Trade dummyProduct = findOrCreateDummyProduct();

        return chatRepository
                .findByBuyerAndSellerAndProduct(buyer, aiUser, dummyProduct)
                .orElseGet(() -> {
                    Chat chat = Chat.builder()
                            .buyer(buyer)
                            .seller(aiUser)
                            .product(dummyProduct)  // null이 아니라 dummyProduct 사용
                            .build();
                    return chatRepository.save(chat);
                });
    }

    @Transactional
    public Trade findOrCreateDummyProduct() {
        // 예시: 기본 카테고리, 판매자 조회 코드
        Category defaultCategory = categoryRepository.findById(1L)
                .orElseThrow(() -> new IllegalStateException("기본 카테고리가 없습니다."));
        User defaultSeller = userService.findByEmail("system@danggeun.com")
                .orElseThrow(() -> new IllegalStateException("시스템 판매자가 없습니다."));

        return tradeRepository.findByTitle("AI Dummy Product")
                .orElseGet(() -> tradeRepository.save(
                        Trade.builder()
                                .category(defaultCategory)
                                .seller(defaultSeller)
                                .name("AI Dummy Product Name")
                                .title("AI Dummy Product")
                                .price(0L)
                                .detail("This is an AI dummy product for chat.")
                                .state("00")
                                .createdAt(LocalDateTime.now())
                                .build()
                ));
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


            String title = chat.getProduct() != null
                    ? chat.getProduct().getTitle()
                    : otherUser.getUsername();

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


    @Transactional
    public Chat findById(Long chatId) {
        return chatRepository.findById(chatId)
                .orElseThrow(() -> new IllegalArgumentException("채팅방을 찾을 수 없습니다."));
    }
}