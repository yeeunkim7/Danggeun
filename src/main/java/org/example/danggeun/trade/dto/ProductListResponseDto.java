package org.example.danggeun.trade.dto;

import lombok.Getter;
import lombok.Setter;
import org.example.danggeun.trade.entity.Trade; // 변환 대상 엔티티

@Getter
@Setter
public class ProductListResponseDto {

    private final Long id;
    private final String title;
    private final Long price;
    private final String imageUrl;
    // 주소 정보는 User 엔티티에 있으므로, 서비스 로직에서 조합해야 합니다.
    private final String address;
    private final int views;
    private final int chats;

    // Entity를 DTO로 변환하는 생성자
    public ProductListResponseDto(Trade entity) {
        this.id = entity.getId();
        this.title = entity.getTitle();
        this.price = entity.getPrice();
        this.imageUrl = entity.getImageUrl();
        this.views = entity.getViews();
        this.chats = entity.getChats();
        if (entity.getSeller() != null && !entity.getSeller().getAddress().isEmpty()) {
            this.address = entity.getSeller().getAddress().get(0).getDetail();
        } else {
            this.address = "주소 정보 없음"; // 기본값 설정
        }
    }
}