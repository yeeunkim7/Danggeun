package org.example.danggeun.write.controller.Dto;

import lombok.Builder;
import org.example.danggeun.write.entity.Write;

import java.time.LocalDateTime;

@Builder
public record WriteUserDto(
        String productNm,
        String productPrice,
        String productDetail,
        String address,
        LocalDateTime productCreatedAt
) {
    public Write toEntity() {
        return Write.builder()
                .productNm(productNm)
                .productPrice(Long.valueOf(productPrice))
                .productDetail(productDetail)
                .address(address)
                .productCreatedAt(LocalDateTime.now())
                .build();
    }
}