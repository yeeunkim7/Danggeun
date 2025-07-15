// User.java
package org.example.danggeun.user.entity;

import jakarta.persistence.*;
import lombok.*;
import org.example.danggeun.address.entity.Address;
import org.example.danggeun.trade.entity.Trade;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "User") // ERD의 테이블명 "User"와 일치시킴
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "User_ID")
    private Long id; // 사용자ID

    @Column(name = "User_Nm", length = 50, nullable = false)
    private String name; // 사용자이름

    @Column(name = "User_Password", length = 100, nullable = false)
    private String password; // 비밀번호

    @Column(name = "User_Email", length = 100, unique = true, nullable = false)
    private String email; // 이메일

    @Column(name = "User_Phone", length = 50)
    private String phone; // 전화번호

    @Column(name = "User_CreatedAt")
    private LocalDateTime createdAt; // 가입일자

    // 한 명의 유저는 여러 개의 상품을 판매할 수 있음
    @OneToMany(mappedBy = "seller", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Trade> products = new ArrayList<>();

    // 한 명의 유저는 여러 개의 주소를 가질 수 있음
    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Address> address = new ArrayList<>();
}
