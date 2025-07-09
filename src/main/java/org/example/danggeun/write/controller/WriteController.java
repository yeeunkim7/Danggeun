package org.example.danggeun.write.controller;

import jakarta.servlet.http.HttpSession;
import org.example.danggeun.write.Dto.WriteUserDto;
import org.example.danggeun.write.service.WriteService;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.util.Map;
import java.util.Base64;

@Controller
@RequestMapping("/write")
public class WriteController {

    private final WriteService writeService;

    public WriteController(WriteService writeService) {
        this.writeService = writeService;
    }

    @GetMapping
    public String writePage() {
        return "write/write";
    }

    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    @ResponseStatus(HttpStatus.CREATED)
    public Map<String, Object> write(@RequestBody WriteUserDto dto, HttpSession session) {
        LocalDateTime createdAt;

        if (dto.getProductCreatedAt() instanceof LocalDateTime) {
            createdAt = dto.getProductCreatedAt();
        } else {
            // 만약 String 타입이라면 파싱
            createdAt = OffsetDateTime.parse(dto.getProductCreatedAt().toString()).toLocalDateTime();
        }

        // 세션 저장
        session.setAttribute("title", dto.getProductNm());
        session.setAttribute("productPrice", Long.parseLong(String.valueOf(dto.getProductPrice())));
        session.setAttribute("productDetail", dto.getProductDetail());
        session.setAttribute("address", dto.getAddress());
        session.setAttribute("imageUrl", "/images/from/base64");
        session.setAttribute("views", 1);
        session.setAttribute("chats", 0);
        session.setAttribute("productCreatedAt", createdAt.toString());

        return Map.of(
                "productNm", dto.getProductNm(),
                "productPrice", dto.getProductPrice(),
                "productDetail", dto.getProductDetail(),
                "address", dto.getAddress(),
                "productImg", dto.getProductImg(),
                "productCreatedAt", createdAt.toString()
        );
    }
}