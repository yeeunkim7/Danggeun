package org.example.danggeun.areaconfirm.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "area", indexes = {
        @Index(name = "idx_area_user_id", columnList = "user_id"),
        @Index(name = "idx_area_user_confirmed", columnList = "user_id, confirmed_at")
})
public class Area {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "area_id")
    private Long areaId;

    @Column(name = "user_id", nullable = false)
    private Long userId;

    @Column(name = "address", length = 500, nullable = false)
    private String address;

    // precision과 scale 제거 - Double 타입은 이미 충분한 정밀도를 제공
    @Column(name = "latitude", nullable = false)
    private Double latitude;

    @Column(name = "longitude", nullable = false)
    private Double longitude;

    @Column(name = "confirmed_at", nullable = false)
    private LocalDateTime confirmedAt;

    @PrePersist
    protected void onCreate() {
        if (this.confirmedAt == null) {
            this.confirmedAt = LocalDateTime.now();
        }
    }
}