package org.example.danggeun.item.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ItemDto {
    private Long id;
    private String title;
    private int price;
    private String location;
    private int viewCount;
    private int chatCount;
    private String imageUrl; // 상품 썸네일 이미지 URL
}