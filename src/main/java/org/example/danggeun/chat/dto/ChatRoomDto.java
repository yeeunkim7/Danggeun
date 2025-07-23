package org.example.danggeun.chat.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.example.danggeun.trade.entity.Trade;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class ChatRoomDto {
    private Long roomId;
    private String otherUserName;
    private String lastMessage;
    private String productTitle;
    private String productImageUrl;
    private Long productPrice;
    private String productState;

    public ChatRoomDto(Long roomId,
                       String otherUserName,
                       String lastMessage,
                       Trade product) {
        this.roomId = roomId;
        this.otherUserName = otherUserName;
        this.lastMessage = lastMessage;
        this.productTitle = product.getTitle();
        this.productImageUrl = product.getImageUrl();
        this.productPrice = product.getPrice();
        this.productState = product.getState();
    }
}
