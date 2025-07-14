// Message.java
package org.example.danggeun.message.entity;

import jakarta.persistence.*;
import lombok.*;
import org.example.danggeun.chat.entity.Chat;
import org.example.danggeun.user.entity.User;

import java.time.LocalDateTime;

@Entity
@Table(name = "Message")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Message {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "Message_ID")
    private Long id; // 메시지ID

    @Lob // TEXT 타입과 매핑
    @Column(name = "Message_detail")
    private String detail; // 메시지내용

    @Column(name = "Message_CreatedAt")
    private LocalDateTime createdAt; // 보낸시간

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "User_Id", nullable = false)
    private User sender; // 보낸사람ID (FK)

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "Chat_ID", nullable = false)
    private Chat chat; // 채팅방ID (FK)
}
