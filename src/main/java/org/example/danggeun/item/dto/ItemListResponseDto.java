package org.example.danggeun.item.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ItemListResponseDto {
    private Long id;
    private String title;
    private int price;
    private String location;
    private int viewCount;
    private int chatCount;
    private String imageUrl;
}