package org.example.danggeun.address.entity;

import jakarta.persistence.*;
import lombok.*;
import org.example.danggeun.user.entity.User;

@Entity
@Table(name = "address")  // 소문자로 변경
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Address {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Lob
    @Column(name = "address_detail", nullable = false)
    private String detail;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)  // User_ID에서 user_id로 변경
    private User user;

    // DB에 존재하는 user_add 컬럼 추가
    @Column(name = "user_add", nullable = false)
    private Long userAdd;

    // user_add 값을 자동으로 설정하는 메서드
    @PrePersist
    protected void onCreate() {
        if (this.userAdd == null && this.user != null) {
            this.userAdd = this.user.getId();  // user_id와 동일한 값으로 설정
        }
    }
}