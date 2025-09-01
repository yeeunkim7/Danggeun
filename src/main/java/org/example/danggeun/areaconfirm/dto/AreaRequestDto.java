package org.example.danggeun.areaconfirm.dto;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AreaRequestDto {

    private Long userId;
    private String address;
    private Double latitude;
    private Double longitude;

    public void validate() {
        if (userId == null) {
            throw new IllegalArgumentException("사용자 ID는 필수입니다.");
        }
        if (address == null || address.trim().isEmpty()) {
            throw new IllegalArgumentException("주소는 필수입니다.");
        }
        if (address.length() > 500) {
            throw new IllegalArgumentException("주소는 500자를 초과할 수 없습니다.");
        }
        if (latitude == null) {
            throw new IllegalArgumentException("위도는 필수입니다.");
        }
        if (longitude == null) {
            throw new IllegalArgumentException("경도는 필수입니다.");
        }
        if (latitude < -90.0 || latitude > 90.0) {
            throw new IllegalArgumentException("위도는 -90도에서 90도 사이여야 합니다.");
        }
        if (longitude < -180.0 || longitude > 180.0) {
            throw new IllegalArgumentException("경도는 -180도에서 180도 사이여야 합니다.");
        }
    }
}