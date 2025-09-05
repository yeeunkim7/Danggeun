package org.example.danggeun.trade.dto;

import lombok.Getter;
import lombok.Setter;
import org.example.danggeun.address.entity.Address;
import org.example.danggeun.trade.entity.Trade;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
public class TradeDetailResponseDto {

    private Long id;
    private String title;
    private Long price;
    private String detail;
    private String state;
    private String imageUrl;
    private LocalDateTime createdAt;
    private int views;
    private int chats;
    private String sellerName;
    private String categoryName;
    private String address;

    public TradeDetailResponseDto() {}

    public TradeDetailResponseDto(Trade entity) {
        this.id = entity.getId();
        this.title = entity.getTitle();
        this.price = entity.getPrice();
        this.detail = entity.getDetail();
        this.state = entity.getState();
        this.imageUrl = entity.getImageUrl();
        this.createdAt = entity.getCreatedAt();

        if (entity.getSeller() != null) {
            this.sellerName = entity.getSeller().getNickname() != null ?
                    entity.getSeller().getNickname() : entity.getSeller().getUsername();
        } else {
            this.sellerName = "알 수 없는 판매자";
        }

        if (entity.getCategory() != null) {
            this.categoryName = entity.getCategory().getName();
        } else {
            this.categoryName = "미분류";
        }

        this.views = entity.getViews();
        this.chats = entity.getChats();

        // 주소 정보 처리
        if (entity.getSeller() != null) {
            List<Address> addrs = entity.getSeller().getAddress();
            if (addrs != null && !addrs.isEmpty()) {
                this.address = addrs.get(0).getDetail();
            } else {
                this.address = "주소 정보 없음";
            }
        } else {
            this.address = "주소 정보 없음";
        }
    }
}