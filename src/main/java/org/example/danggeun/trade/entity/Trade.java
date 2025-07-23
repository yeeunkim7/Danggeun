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
    @JoinColumn(name = "user_id", nullable = false)
    private User seller;

    @Column(name = "product_name", length = 50)
    private String name;

    @Column(name = "product_price")
    private Long price;

    @Lob
    @Column(name = "product_detail")
    private String detail;

    @Column(name = "product_state", length = 2)
    private String state = "00";

    @Column(name = "product_created_at")
    private LocalDateTime createdAt;

    @Column(name = "image_url")
    private String imageUrl;

    @Column(name = "title")
    private String title;

    @Column(columnDefinition = "INT DEFAULT 0")
    private int views = 0;

    @Column(columnDefinition = "INT DEFAULT 0")
    private int chats = 0;

    public void increaseViewCount() {
        this.views++;
    }

    public void increaseChatCount() {
        this.chats++;
    }
}