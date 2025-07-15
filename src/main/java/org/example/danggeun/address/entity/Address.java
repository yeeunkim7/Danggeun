// Address.java
package org.example.danggeun.address.entity;

import jakarta.persistence.*;
import lombok.*;
import org.example.danggeun.user.entity.User;

@Entity
@Table(name = "Address")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Address {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id; // 주소 ID (ERD에는 없지만, PK는 있는 것이 표준적입니다)

    @Lob // ERD의 `User_Add` 컬럼을 주소 상세 정보로 해석
    @Column(name = "address_detail", nullable = false)
    private String detail; // 유저위치 (상세 주소)

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "User_ID", nullable = false)
    private User user; // 사용자ID (FK)
}
