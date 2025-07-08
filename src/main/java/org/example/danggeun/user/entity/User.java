package org.example.danggeun.user.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "users")  // 예약어 회피용으로 테이블명 변경
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String email;
    private String name;
    private String provider;
    private String providerId;
}