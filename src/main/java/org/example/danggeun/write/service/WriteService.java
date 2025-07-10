package org.example.danggeun.write.service;

import lombok.RequiredArgsConstructor;
import org.example.danggeun.write.Dto.WriteUserDto;
import org.example.danggeun.write.entity.Write;
import org.example.danggeun.write.repository.WriteRepository;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class WriteService {

    private final WriteRepository writeRepository;

    public void save(WriteUserDto dto, Long userId, Long categoryId) throws IOException {
        String originalFilename = dto.getProductImage().getOriginalFilename();
        String storedFileName = UUID.randomUUID() + "_" + originalFilename;

        // ✅ 실제 이미지 저장할 외부 절대 경로
        String uploadDir = "/Users/user/uploads"; // ⚠️ 사용자 경로에 맞게 수정 필요
        String fullPath = uploadDir + "/" + storedFileName;

        File dest = new File(fullPath);
        dest.getParentFile().mkdirs(); // 폴더 없으면 생성
        dto.getProductImage().transferTo(dest);

        String webPath = "/uploads/" + storedFileName;

        Write write = Write.builder()
                .productNm(dto.getProductNm())
                .productPrice(dto.getProductPrice())
                .productDetail(dto.getProductDetail())
                .address(dto.getAddress())
                .productImg(webPath) // 상대경로만 저장
                .productCreatedAt(LocalDateTime.now())
                .userId2(userId)
                .categoryId(categoryId)
                .build();

        writeRepository.save(write);
    }
}