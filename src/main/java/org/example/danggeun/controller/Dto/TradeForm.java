package org.example.danggeun.controller.Dto;

import org.springframework.web.multipart.MultipartFile;

public record TradeForm(
        String title,
        String productPrice,
        String productDetail,
        String address,
        MultipartFile productImage
) {}
