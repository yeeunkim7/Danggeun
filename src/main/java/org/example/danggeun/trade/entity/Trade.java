package org.example.danggeun.trade.entity;

import jakarta.persistence.*;
import lombok.*;
import org.example.danggeun.category.entity.Category;
import org.example.danggeun.user.entity.User;
// import org.springframework.web.multipart.MultipartFile; // 엔티티는 웹 기술(MultipartFile)에 의존하지 않아야 합니다.

import java.time.LocalDateTime;

@Getter
@Setter // 엔티티의 무분별한 수정을 막기 위해 @Setter는 사용하지 않는 것이 좋습니다. 꼭 필요한 경우에만 특정 필드에 대한 수정 메소드를 만드세요.
@Entity
@Builder
@Table(name = "product")
@NoArgsConstructor
@AllArgsConstructor
public class Trade {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "product_id") // 수정: product_id (소문자 스네이크 케이스)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "category_id", nullable = false) // 수정: category_id
    private Category category;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false) // 수정: user_nm -> user_id 로 통일성 부여
    private User seller;

    @Column(name = "product_name", length = 50) // 수정: product_nm -> product_name 으로 명확하게
    private String name;

    @Column(name = "product_price")
    private Long price;

    @Lob
    @Column(name = "product_detail")
    private String detail;

    @Column(name = "product_state", length = 2)
    private String state = "00";

    @Column(name = "product_created_at") // 수정: created_at (가장 일반적인 이름)
    private LocalDateTime createdAt;

    @Column(name = "image_url") // 수정: image_url
    private String imageUrl;

    @Column(name = "title") // 수정: product_title -> title (테이블명 접두사는 불필요)
    private String title;

    @Column(columnDefinition = "INT DEFAULT 0") // 'views'는 예약어일 수 있으므로 그대로 둠
    private int views = 0;

    @Column(columnDefinition = "INT DEFAULT 0") // 'chats'도 마찬가지
    private int chats = 0;

    public void increaseViewCount() {
        this.views++;
    }

    public void increaseChatCount() {
        this.chats++;
    }
}