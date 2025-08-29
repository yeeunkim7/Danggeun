package org.example.danggeun.trade.dto;

import lombok.Getter;
import lombok.Setter;
import org.example.danggeun.trade.entity.Trade;
import org.example.danggeun.category.entity.Category;
import org.example.danggeun.user.entity.User;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;

@Getter
@Setter
public class TradeCreateRequestDto {

    private String title;
    private String name;
    private Long price;
    private String detail;
    private Long categoryId;
    private MultipartFile image;
    private String address;

    public Trade toEntity(User seller, Category category, String imageUrl) {
        return Trade.builder()
                .title(this.title)
                .detail(this.detail)
                .price(this.price)
                .imageUrl(imageUrl)
                .state("00")
                .createdAt(LocalDateTime.now())
                .seller(seller)
                .category(category)
                .build();
    }
}
