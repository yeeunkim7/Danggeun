package org.example.danggeun.areaConfirm.Dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AreaDto {

    private Long userId;        // 인증한 사용자 ID
    private String address;     // 주소
    private double latitude;    // 위도
    private double longitude;   // 경도
}