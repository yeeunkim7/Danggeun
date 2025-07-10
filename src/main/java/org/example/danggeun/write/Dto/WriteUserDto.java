package org.example.danggeun.write.Dto;

import lombok.*;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class WriteUserDto {
    private String productNm;           // 글 제목₩
    private Long productPrice;          // 가격
    private String productDetail;       // 상세설명
    private String address;             // 거래 희망 장소
    private MultipartFile productImage; // 이미지 파일 (form name과 일치)
    private LocalDateTime productCreatedAt; // 등록시간 (optional)
}