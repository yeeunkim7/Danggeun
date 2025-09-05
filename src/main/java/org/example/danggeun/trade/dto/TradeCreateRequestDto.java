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
    private String productNm; // HTML 폼과 매핑용 (호환성)
    private Long price;
    private Long productPrice; // HTML 폼과 매핑용 (호환성)
    private String detail;
    private String productDetail; // HTML 폼과 매핑용 (호환성)
    private String address;
    private Long categoryId;
    private MultipartFile image;
    private MultipartFile productImage; // HTML 폼과 매핑용 (호환성)

    // HTML 폼 데이터를 정규화하는 메서드들
    public String getNormalizedTitle() {
        return title != null ? title : productNm;
    }

    public Long getNormalizedPrice() {
        return price != null ? price : productPrice;
    }

    public String getNormalizedDetail() {
        return detail != null ? detail : productDetail;
    }

    public MultipartFile getNormalizedImage() {
        return image != null ? image : productImage;
    }

    // 유효성 검사 메서드
    public boolean isValid() {
        return getNormalizedTitle() != null && !getNormalizedTitle().trim().isEmpty() &&
                getNormalizedPrice() != null && getNormalizedPrice() > 0 &&
                getNormalizedDetail() != null && !getNormalizedDetail().trim().isEmpty() &&
                address != null && !address.trim().isEmpty() &&
                categoryId != null;
    }

    public Trade toEntity(User seller, Category category, String imageUrl) {
        return Trade.builder()
                .title(getNormalizedTitle())
                .detail(getNormalizedDetail())
                .price(getNormalizedPrice())
                .address(this.address)
                .imageUrl(imageUrl)
                .state("00")
                .createdAt(LocalDateTime.now())
                .seller(seller)
                .category(category)
                .build();
    }
}