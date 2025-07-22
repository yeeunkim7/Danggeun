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
    private String title;        // 타이틀: AI챗이면 "AI 챗", 거래챗이면 상품명
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

        // 상품(chat.getProduct())이 null 이면 AI챗, 아니면 거래챗
        if (chat.getProduct() != null) {
            this.title = chat.getProduct().getName();  // product_nm
        } else {
            this.title = "AI 챗";  // 또는 원하는 기본 제목
        }
    }
}

