package org.example.danggeun.write.controller;

import org.example.danggeun.write.Dto.WriteUserDto;
import org.example.danggeun.write.service.WriteService;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.multipart.MultipartFile;
import java.time.OffsetDateTime;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.Base64;
import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("/write")
public class WriteController {

    private final WriteService writeService;

    public WriteController(WriteService writeService) {
        this.writeService = writeService;
    }

    // ✅ 1. HTML 페이지 렌더링
    @GetMapping
    public String writePage() {
        return "write/write"; // templates/write/write.html 로 이동
    }

    // ✅ 2. API 요청 처리 (JSON 본문 받고, 문자열 응답)
    @PostMapping
    @ResponseBody
    @ResponseStatus(HttpStatus.CREATED)
    public Map<String, String> write(
            @RequestPart("productNm") String productNm,
            @RequestPart("productPrice") String productPrice,
            @RequestPart("productDetail") String productDetail,
            @RequestPart("address") String address,
            @RequestPart("productCreatedAt") String productCreatedAt,
            @RequestPart("productImg") MultipartFile productImg
    ) throws IOException {
        byte[] imageBytes = productImg.getBytes();
        // Parse ISO_INSTANT string (e.g., 2025-07-08T06:48:28.548Z) to LocalDateTime
        LocalDateTime createdAt = OffsetDateTime.parse(productCreatedAt).toLocalDateTime();

        WriteUserDto dto = WriteUserDto.builder()
                .productNm(productNm)
                .productPrice(productPrice)
                .productDetail(productDetail)
                .address(address)
                .productImg(Base64.getEncoder().encodeToString(imageBytes))
                .productCreatedAt(createdAt)
                .build();

        writeService.save(dto, 1L, 1L);
        return Map.of("message", "저장 완료");
    }
}