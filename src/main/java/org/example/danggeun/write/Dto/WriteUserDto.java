package org.example.danggeun.write.Dto;

import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class WriteUserDto {
    private String productNm;
    private Long productPrice;
    private String productDetail;
    private String address;
    private String productImg; // base64 encoded
    private LocalDateTime productCreatedAt;
}