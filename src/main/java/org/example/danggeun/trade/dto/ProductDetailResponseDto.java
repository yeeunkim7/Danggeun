package org.example.danggeun.trade.dto;

import lombok.Getter;
import lombok.Setter;
import org.example.danggeun.trade.entity.Trade;
import java.time.LocalDateTime;

@Getter
@Setter
public class ProductDetailResponseDto {

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

    // Entity를 DTO로 변환하는 생성자
    public ProductDetailResponseDto(Trade entity, int views, int chats) {
        this.id = entity.getId();
        this.title = entity.getTitle();
        this.name = entity.getName();
        this.price = entity.getPrice();
        this.detail = entity.getDetail();
        this.state = entity.getState();
        this.imageUrl = entity.getImageUrl();
        this.createdAt = entity.getCreatedAt();

        // 연관된 엔티티에서 필요한 정보를 추출합니다.
        this.sellerName = entity.getSeller().getName();
        this.categoryName = entity.getCategory().getName();
        this.views = entity.getViews();
        this.chats = entity.getChats();
    }
}