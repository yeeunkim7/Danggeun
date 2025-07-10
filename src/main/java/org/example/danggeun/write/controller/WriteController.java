package org.example.danggeun.write.controller;

import jakarta.servlet.http.HttpSession;
import org.example.danggeun.write.Dto.WriteUserDto;
import org.example.danggeun.write.service.WriteService;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
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
    public String writePage() { //글쓰기 HTML 폼 페이지를 렌더링함
        return "write/write";
    }

    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @ResponseStatus(HttpStatus.SEE_OTHER)
    public String handleFormSubmit(@ModelAttribute WriteUserDto dto,
                                   HttpSession session) throws IOException {
        // DTO에 MultipartFile 들어있음 (productImage)
        writeService.save(dto, 1L, 1L); // userId, categoryId는 실제 로그인값이나 입력값으로 바꾸세요
        System.out.println("productNm: " + dto.getProductNm());
        System.out.println("productPrice: " + dto.getProductPrice());
        System.out.println("productDetail: " + dto.getProductDetail());
        System.out.println("address: " + dto.getAddress());
        return "redirect:/trade/post";
    }
}