// Chat.java
package org.example.danggeun.chat.entity;

import jakarta.persistence.*;
import lombok.*;
import org.example.danggeun.message.entity.Message;
import org.example.danggeun.trade.entity.Trade;
import org.example.danggeun.user.entity.User;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "Chat")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Chat {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "Chat_ID")
    private Long id; // 채팅방ID

    @Column(name = "Chat_CreatedAt")
    private LocalDateTime createdAt; // 채팅생성일자

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "Buyer_Id", nullable = false)
    private User buyer; // 구매자ID (FK)

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "Seller_Id", nullable = false)
    private User seller; // 판매자ID (FK)

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "Product_ID", nullable = false)
    private Trade product; // 상품ID (FK)

    // 하나의 채팅방은 여러 메시지를 가질 수 있음
    @OneToMany(mappedBy = "chat", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Message> messages = new ArrayList<>();
}
