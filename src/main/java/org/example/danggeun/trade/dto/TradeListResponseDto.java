package org.example.danggeun.trade.dto;

import lombok.Getter;
import lombok.Setter;
import org.example.danggeun.trade.entity.Trade;

@Getter
@Setter
public class TradeListResponseDto {

    private Long id;
    private String title;
    private Long price;
    private String imageUrl;
    private String address;
    private int views;
    private int chats;

    public TradeListResponseDto() {}

    public TradeListResponseDto(Trade entity) {
        this.id = entity.getId();
        this.title = entity.getTitle();
        this.price = entity.getPrice();
        this.imageUrl = entity.getImageUrl();
        this.views = entity.getViews();
        this.chats = entity.getChats();

        // 주소 정보 안전 처리
        if (entity.getSeller() != null &&
                entity.getSeller().getAddress() != null &&
                !entity.getSeller().getAddress().isEmpty()) {
            this.address = entity.getSeller().getAddress().get(0).getDetail();
        } else {
            this.address = "주소 정보 없음";
        }
    }
}