package org.example.danggeun.chat.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.example.danggeun.chat.entity.Chat;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ChatSummaryDto {
    private Long chatId;
    private String title;
    private String lastMessage;
    private Long buyerId;
    private Long sellerId;
    private Long opponentId;
    private boolean isAiChat;
    private long unreadCount;

    public ChatSummaryDto(Chat chat, Long currentUserId, String lastMessage) {
        this.chatId = chat.getId();
        this.lastMessage = lastMessage != null ? lastMessage : "";
        this.buyerId = chat.getBuyer().getId();
        this.sellerId = chat.getSeller().getId();
        this.opponentId = buyerId.equals(currentUserId) ? sellerId : buyerId;
        this.isAiChat = chat.getProduct() == null;
        this.unreadCount = 0; // 기본값, 필요시 별도 조회

        if (chat.getProduct() != null) {
            this.title = chat.getProduct().getTitle();
        } else {
            // AI 채팅인 경우
            String aiEmail = "system@danggeun.com";
            if (aiEmail.equals(chat.getSeller().getEmail()) || aiEmail.equals(chat.getBuyer().getEmail())) {
                this.title = "AI 도우미";
            } else {
                this.title = currentUserId.equals(chat.getBuyer().getId())
                        ? chat.getSeller().getUsername()
                        : chat.getBuyer().getUsername();
            }
        }
    }
}