package org.example.danggeun.trade.dto;

import lombok.Getter;
import org.example.danggeun.trade.entity.Trade;
import java.time.LocalDateTime;

@Getter
public class TradeDetailResponseDto {

    private final Long id;
    private final String title;
    private final String name;
    private final Long price;
    private final String detail;
    private final String state;
    private final String imageUrl;
    private final LocalDateTime createdAt;
    private final int views;
    private final int chats;
    private final String sellerName;
    private final String categoryName;

    public TradeDetailResponseDto(Trade entity) {
        this.id = entity.getId();
        this.title = entity.getTitle();
        this.name = entity.getName();
        this.price = entity.getPrice();
        this.detail = entity.getDetail();
        this.state = entity.getState();
        this.imageUrl = entity.getImageUrl();
        this.createdAt = entity.getCreatedAt();
        this.sellerName = entity.getSeller().getUsername();
        this.categoryName = entity.getCategory().getName();
        this.views = entity.getViews();
        this.chats = entity.getChats();

    }
}