package org.example.danggeun.user.entity;

import jakarta.persistence.*;
import lombok.*;
import org.example.danggeun.address.entity.Address;
import org.example.danggeun.trade.entity.Trade;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "\"user\"")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "user_id")
    private Long id;

    @Column(name = "user_nm", length = 50, nullable = false)
    private String username;

    @Column(name = "user_password", length = 100, nullable = false)
    private String password;

    @Column(name = "user_email", length = 100, unique = true, nullable = false)
    private String email;

    @Column(name = "user_phone", length = 50)
    private String phone;

    @Column(name = "user_createdat")
    private LocalDateTime createdAt;

    @Column(nullable = true)
    private String provider;

    @Column(nullable = true)
    private String providerId;

    @Column(nullable = true)
    private String role;

    private String nickname;

    private String profileImageUrl;

    private String region;

    @OneToMany(mappedBy = "seller", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Trade> products = new ArrayList<>();

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Address> address = new ArrayList<>();

    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
    }
}
