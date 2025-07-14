package org.example.danggeun.write.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Lob;
import jakarta.persistence.Table;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@Entity
@Builder
@Table(name = "product")
public class Write {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "product_id")
    private Long productId; // 상품 ID

    @Column(name = "category_id", nullable = false)
    private Long categoryId; // 카테고리 ID

    @Column(name = "user_id2", nullable = false)
    private Long userId2; // 등록자 ID

    @Column(name = "product_nm", length = 50)
    private String productNm; // 상품 이름

    @Column(name = "product_price", length = 20)
    private Long productPrice; // 상품 가격

    @Lob
    @Column(name = "product_detail", length = 200)
    private String productDetail; // 상품 설명

    @Column(name = "product_state", length = 2)
    private String productState = "00"; // 판매 여부: 00-판매중, 01-예약중, 02-판매완료

    @Column(name = "product_created_at")
    private LocalDateTime productCreatedAt; // 등록 일자

    @Lob
    @Column(name = "address", length = 200)
    private String address; // 주소 설명

    @Lob
    @Column(name = "product_img")
    private byte[] productImg; // 상품 이미지

    public Write(Long productId, Long categoryId, Long userId2, String productNm,
                 Long productPrice, String productDetail, String productState,
                 LocalDateTime productCreatedAt, String address, byte[] productImg) {
        this.productId = productId;
        this.categoryId = categoryId;
        this.userId2 = userId2;
        this.productNm = productNm;
        this.productPrice = productPrice;
        this.productDetail = productDetail;
        this.productState = productState;
        this.productCreatedAt = productCreatedAt;
        this.address = address;
        this.productImg = productImg;
    }
}
