package org.example.danggeun.trade.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.web.multipart.MultipartFile;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class TradeDto {
    private Long productId;
    private String title;
    private Long productPrice;
    private String productDetail;
    private String address;
    private String imageUrl;
    private String productNm;
    private int views;
    private int chats;
}