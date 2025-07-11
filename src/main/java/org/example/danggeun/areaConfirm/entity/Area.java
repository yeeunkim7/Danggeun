package org.example.danggeun.areaConfirm.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Lob;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "area")
public class Area {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "area_id")
    private Long areaId;                       // 동네 인증 PK

    @Column(name = "user_id", nullable = false)
    private Long userId;                       // 인증한 사용자 ID (자체·구글 공통)

    @Lob
    @Column(name = "address", length = 200)
    private String address;                    // 주소(법정동·행정동 등)

    @Column(name = "latitude", nullable = false)
    private double latitude;                   // 위도

    @Column(name = "longitude", nullable = false)
    private double longitude;                  // 경도

    @Column(name = "confirmed_at")
    private LocalDateTime confirmedAt;         // 인증 일시

    // ────────────────────────────────────────────────
    // Entity Lifecycle
    // ────────────────────────────────────────────────
    @PrePersist
    protected void onCreate() {                // 저장 직전 자동 세팅
        this.confirmedAt = LocalDateTime.now();
    }
}