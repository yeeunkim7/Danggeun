package org.example.danggeun.chat.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import org.example.danggeun.chat.entity.Chat;

@Data
@Builder
@AllArgsConstructor
public class ChatSummaryDto {
    private Long chatId;
    private String title;
    private String lastMessage;
    private Long buyerId;
    private Long sellerId;
    private Long opponentId;

    public ChatSummaryDto(Chat chat, Long currentUserId, String lastMessage) {
        this.chatId      = chat.getId();
        this.lastMessage = lastMessage;
        this.buyerId     = chat.getBuyer().getId();
        this.sellerId    = chat.getSeller().getId();
        this.opponentId  = buyerId.equals(currentUserId) ? sellerId : buyerId;

        if (chat.getProduct() != null) {
            this.title = chat.getProduct().getTitle();  // ← getName() → getTitle()
        } else {
            this.title = "AI 챗";
        }
    }
}
