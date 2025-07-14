package org.example.danggeun.trade.entity;

import jakarta.persistence.*;
import lombok.*;
import org.example.danggeun.category.entity.Category;
import org.example.danggeun.user.entity.User;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;

@Getter
@Setter
@Entity
@Builder
@Table(name = "product")
@NoArgsConstructor
@AllArgsConstructor
public class Trade {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "Product_ID")
    private Long id; // 상품ID

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "Category_ID", nullable = false)
    private Category category; // 카테고리ID (FK)

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "User_Id", nullable = false)
    private User seller; // 판매자ID (FK)

    @Column(name = "Product_Nm", length = 50)
    private String name; // 상품이름

    @Column(name = "Product_price")
    private Long price; // 상품가격

    @Lob
    @Column(name = "Product_detail")
    private String detail; // 상품설명

    @Column(name = "Product_State", length = 2)
    private String state = "00"; // 판매여부 (00:판매중, 01:예약중, 02:판매완료)

    @Column(name = "Product_CreatedAt")
    private LocalDateTime createdAt; // 상품등록일자

    @Column(name = "Product_Img_Url") // ERD의 Product_Img_Url 컬럼 반영
    private String imageUrl; // 상품이미지 URL

    @Column(name = "Product_Title")
    private String title;

    @Column(name = "views", columnDefinition = "INT DEFAULT 0")
    private int views = 0; // 조회수

    @Column(name = "chats", columnDefinition = "INT DEFAULT 0")
    private int chats = 0; // 채팅수

    public void increaseViewCount() {
        this.views++;
    }

    public void increaseChatCount() {
        this.chats++;
    }
}
