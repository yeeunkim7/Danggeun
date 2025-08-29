package org.example.danggeun.trade.dto;

import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@Builder
public class ItemSearchDto {
    private Long id;
    private String title;
    private String content;
    private int price;
    private String imageUrl;
    private String location;
    private LocalDateTime createdAt;
}