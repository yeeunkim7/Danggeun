package org.example.danggeun.trade.entity;

import jakarta.persistence.*;
import lombok.*;
import org.example.danggeun.category.entity.Category;
import org.example.danggeun.user.entity.User;

import java.time.LocalDateTime;

@Getter
@Entity
@Builder
@Table(name = "product")
@NoArgsConstructor
@AllArgsConstructor
public class Trade {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "product_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "category_id", nullable = false)
    private Category category;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)  // user_id2에서 user_id로 수정
    private User seller;

    @Column(name = "title", nullable = false, length = 100)
    private String title;

    @Column(name = "product_detail", columnDefinition = "TEXT", nullable = false)
    private String detail;

    @Column(name = "product_price", nullable = false)
    private Long price;

    @Column(name = "address", length = 100)
    private String address;

    @Builder.Default
    @Column(name = "product_state", length = 2)
    private String state = "00";

    @Column(name = "product_created_at", updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "image_url", length = 500)
    private String imageUrl;

    @Builder.Default
    @Column(name = "views", columnDefinition = "INT DEFAULT 0")
    private int views = 0;

    @Builder.Default
    @Column(name = "chats", columnDefinition = "INT DEFAULT 0")
    private int chats = 0;

    // 비즈니스 메서드들
    public void increaseViewCount() {
        this.views++;
    }

    public void increaseChatCount() {
        this.chats++;
    }

    /**
     * 상품 정보 업데이트
     */
    public void updateInfo(String title, String detail, Long price, String imageUrl, String address) {
        if (title != null && !title.trim().isEmpty()) {
            this.title = title.trim();
        }
        if (detail != null && !detail.trim().isEmpty()) {
            this.detail = detail.trim();
        }
        if (price != null && price > 0) {
            this.price = price;
        }
        if (imageUrl != null && !imageUrl.trim().isEmpty()) {
            this.imageUrl = imageUrl;
        }
        if (address != null && !address.trim().isEmpty()) {
            this.address = address.trim();
        }
    }

    /**
     * 상품 상태 업데이트
     */
    public void updateState(String state) {
        if (state != null && (state.equals("00") || state.equals("01") || state.equals("02"))) {
            this.state = state;
        }
    }

    /**
     * 판매 완료 처리
     */
    public void markAsSold() {
        this.state = "02";
    }

    /**
     * 예약 중 처리
     */
    public void markAsReserved() {
        this.state = "01";
    }

    /**
     * 판매 중 처리
     */
    public void markAsOnSale() {
        this.state = "00";
    }

    /**
     * 판매 상태 확인
     */
    public boolean isOnSale() {
        return "00".equals(this.state);
    }

    public boolean isReserved() {
        return "01".equals(this.state);
    }

    public boolean isSold() {
        return "02".equals(this.state);
    }

    /**
     * 상품 설명 반환 (호환성을 위한 메서드)
     */
    public String getDescription() {
        return this.detail;
    }

    /**
     * 상품 상태 텍스트 반환
     */
    public String getStateText() {
        return switch (this.state) {
            case "00" -> "판매중";
            case "01" -> "예약중";
            case "02" -> "판매완료";
            default -> "알 수 없음";
        };
    }

    /**
     * 엔티티 생성 시 기본값 설정
     */
    @PrePersist
    protected void onCreate() {
        if (this.createdAt == null) {
            this.createdAt = LocalDateTime.now();
        }
        if (this.state == null || this.state.isEmpty()) {
            this.state = "00";
        }
        if (this.views < 0) {
            this.views = 0;
        }
        if (this.chats < 0) {
            this.chats = 0;
        }
    }

    /**
     * 엔티티 수정 시 유효성 검사
     */
    @PreUpdate
    protected void onUpdate() {
        if (this.title != null) {
            this.title = this.title.trim();
        }
        if (this.detail != null) {
            this.detail = this.detail.trim();
        }
        if (this.address != null) {
            this.address = this.address.trim();
        }
    }
}