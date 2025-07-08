package org.example.danggeun.trade.service;

import java.io.File;
import java.io.IOException;
import java.util.UUID;
import jakarta.servlet.http.HttpSession;
import org.example.danggeun.trade.dto.TradeDto;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Service
public class TradeService {

    private static final Logger log = LoggerFactory.getLogger(TradeService.class);

    @Value("${file.upload-dir}")
    private String uploadDir;

    /**
     * DTO로부터 받은 데이터를 파일로 저장하고, 나머지 정보를 HttpSession에 저장합니다.
     * @param tradeDto 사용자가 입력한 폼 데이터
     * @param session HTTP 세션
     */
    public void submitAndStoreInSession(TradeDto tradeDto, HttpSession session) {
        MultipartFile imageFile = tradeDto.getProductImg();

        if (imageFile == null || imageFile.isEmpty()) {
            throw new IllegalArgumentException("상품 이미지는 필수입니다.");
        }

        // 1. 파일 저장
        String originalFilename = imageFile.getOriginalFilename();
        String storedFileName = UUID.randomUUID().toString() + "_" + originalFilename;
        String fileUrl = "/uploads/" + storedFileName;

        try {
            File dest = new File(uploadDir + File.separator + storedFileName);
            dest.getParentFile().mkdirs();
            imageFile.transferTo(dest);
            log.info("파일 저장 성공: {}", dest.getAbsolutePath());
        } catch (IOException e) {
            log.error("파일 저장 실패", e);
            throw new RuntimeException("이미지 저장에 실패했습니다.", e);
        }

        // 2. 세션에 데이터 저장
        session.setAttribute("title", tradeDto.getTitle());
        session.setAttribute("productPrice", tradeDto.getProductPrice());
        session.setAttribute("productDetail", tradeDto.getProductDetail());
        session.setAttribute("address", tradeDto.getAddress());
        session.setAttribute("imageUrl", fileUrl); // 저장된 파일의 URL
        session.setAttribute("views", 1); // 최초 조회수 1로 설정
        session.setAttribute("chats", 0); // 최초 채팅수 0으로 설정
    }

    /**
     * HttpSession에서 게시글 정보를 읽어와 DTO로 반환합니다.
     * @param session HTTP 세션
     * @return 세션 정보를 담은 DTO
     */
    public TradeDto getPostFromSession(HttpSession session) {
        TradeDto dto = new TradeDto();
        dto.setTitle((String) session.getAttribute("title"));
        dto.setProductPrice((String) session.getAttribute("productPrice"));
        dto.setProductDetail((String) session.getAttribute("productDetail"));
        dto.setAddress((String) session.getAttribute("address"));
        dto.setImageUrl((String) session.getAttribute("imageUrl"));

        Integer views = (Integer) session.getAttribute("views");
        dto.setViews(views != null ? views : 0);

        Integer chats = (Integer) session.getAttribute("chats");
        dto.setChats(chats != null ? chats : 0);

        // 상세 페이지를 볼 때마다 조회수 증가
        if(views != null) {
            session.setAttribute("views", views + 1);
        }

        return dto;
    }

    /**
     * 세션의 채팅 수를 1 증가시킵니다.
     * @param session HTTP 세션
     */
    public void increaseChatInSession(HttpSession session) {
        Integer chats = (Integer) session.getAttribute("chats");
        if (chats == null) {
            session.setAttribute("chats", 1);
        } else {
            session.setAttribute("chats", chats + 1);
        }
    }
}