package org.example.danggeun.areaconfirm.dto;

import lombok.*;
import org.example.danggeun.areaconfirm.entity.Area;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AreaResponseDto {

    private Long areaId;
    private Long userId;
    private String address;
    private Double latitude;
    private Double longitude;
    private LocalDateTime confirmedAt;

    public static AreaResponseDto from(Area area) {
        return AreaResponseDto.builder()
                .areaId(area.getAreaId())
                .userId(area.getUserId())
                .address(area.getAddress())
                .latitude(area.getLatitude())
                .longitude(area.getLongitude())
                .confirmedAt(area.getConfirmedAt())
                .build();
    }
}
