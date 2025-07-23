package org.example.danggeun.trade.dto;

import lombok.Getter;
import lombok.Setter;
import org.example.danggeun.address.entity.Address;
import org.example.danggeun.trade.entity.Trade;
import java.time.LocalDateTime;
import java.util.List;

@Getter
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
    private final String address;

    // Entity를 DTO로 변환하는 생성자
    public ProductDetailResponseDto(Trade entity) {
        this.id = entity.getId();
        this.title = entity.getTitle();
        this.name = entity.getName();
        this.price = entity.getPrice();
        this.detail = entity.getDetail();
        this.state = entity.getState();
        this.imageUrl = entity.getImageUrl();
        this.createdAt = entity.getCreatedAt();

        // 연관된 엔티티에서 필요한 정보를 추출합니다.
        this.sellerName = entity.getSeller().getUsername();
        this.categoryName = entity.getCategory().getName();
        this.views = entity.getViews();
        this.chats = entity.getChats();

        List<Address> addrs = entity.getSeller().getAddress();
        if (addrs != null && !addrs.isEmpty()) {
            // 예: 최신 등록된 주소를 쓰고 싶다면 스트림 정렬 후 가져오기
            this.address = addrs.get(0).getDetail();
        } else {
            this.address = "주소 정보 없음";
        }

    }
}