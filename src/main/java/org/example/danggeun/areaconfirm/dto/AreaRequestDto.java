package org.example.danggeun.areaconfirm.dto;

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
public class AreaRequestDto {

    private Long userId;
    private String address;
    private double latitude;
    private double longitude;
}