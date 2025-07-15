package org.example.danggeun.trade.dto;

import lombok.Getter;
import lombok.Setter;
import org.example.danggeun.trade.entity.Trade; // 실제로는 Trade 엔티티를 사용
import org.example.danggeun.category.entity.Category;
import org.example.danggeun.user.entity.User;
import org.springframework.web.multipart.MultipartFile;
import java.time.LocalDateTime;

@Getter
@Setter
public class ProductCreateRequestDto {

    private String title;
    private String name;
    private Long price;
    private String detail;
    private Long categoryId;
    private MultipartFile image;

    // 이 DTO를 Product(Trade) 엔티티로 변환하는 메소드
    // Service 레이어에서 호출됩니다.
    public Trade toEntity(User seller, Category category, String imageUrl) {
        return Trade.builder()
                .title(this.title)
                .name(this.name)
                .price(this.price)
                .detail(this.detail)
                .imageUrl(imageUrl) // 파일 저장 후 얻은 URL
                .state("00") // '판매중' 상태로 기본 설정
                .createdAt(LocalDateTime.now())
                .seller(seller) // 현재 로그인된 사용자 엔티티
                .category(category) // 조회해온 카테고리 엔티티
                .build();
    }
}