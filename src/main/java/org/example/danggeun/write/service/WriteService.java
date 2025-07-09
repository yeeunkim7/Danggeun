package org.example.danggeun.write.service;

import lombok.RequiredArgsConstructor;
import org.example.danggeun.write.Dto.WriteUserDto;
import org.example.danggeun.write.entity.Write;
import org.example.danggeun.write.repository.WriteRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Base64;

@Service
@RequiredArgsConstructor
public class WriteService {

    private final WriteRepository writeRepository;

    public void save(WriteUserDto dto, Long userId, Long categoryId) {
        Write write = Write.builder()
                .productNm(dto.getProductNm())
                .productPrice(dto.getProductPrice())
                .productDetail(dto.getProductDetail())
                .address(dto.getAddress())
                .productImg(dto.getProductImg())
                .productCreatedAt(LocalDateTime.now())
                .userId2(userId)
                .categoryId(categoryId)
                .build();

        writeRepository.save(write);
    }
}