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

    public void submitAndStoreInSession(TradeDto dto, MultipartFile imageFile, HttpSession session) {
        if (imageFile == null || imageFile.isEmpty()) {
            throw new IllegalArgumentException("이미지는 필수입니다.");
        }

        String originalFilename = imageFile.getOriginalFilename();
        String storedFileName = UUID.randomUUID() + "_" + originalFilename;
        String fileUrl = "/uploads/" + storedFileName;

        try {
            File dest = new File(uploadDir + File.separator + storedFileName);
            dest.getParentFile().mkdirs(); // 디렉터리 없으면 생성
            imageFile.transferTo(dest);
            log.info("파일 저장 성공: {}", dest.getAbsolutePath());
        } catch (IOException e) {
            log.error("파일 저장 실패", e);
            throw new RuntimeException("파일 저장 실패", e);
        }

        session.setAttribute("title", dto.getTitle());
        session.setAttribute("productPrice", dto.getProductPrice());
        session.setAttribute("productDetail", dto.getProductDetail());
        session.setAttribute("address", dto.getAddress());
        session.setAttribute("imageUrl", fileUrl); // 업로드된 이미지 경로 저장
        session.setAttribute("views", 1);
        session.setAttribute("chats", 0);
    }

    public TradeDto getPostFromSession(HttpSession session) {
        TradeDto dto = new TradeDto();
        dto.setTitle((String) session.getAttribute("title"));
        dto.setProductPrice((Long) session.getAttribute("productPrice"));
        dto.setProductDetail((String) session.getAttribute("productDetail"));
        dto.setAddress((String) session.getAttribute("address"));
        dto.setImageUrl((String) session.getAttribute("imageUrl"));

        Integer views = (Integer) session.getAttribute("views");
        dto.setViews(views != null ? views : 0);

        Integer chats = (Integer) session.getAttribute("chats");
        dto.setChats(chats != null ? chats : 0);

        if (views != null) {
            session.setAttribute("views", views + 1);
        }

        return dto;
    }

    public void increaseChatInSession(HttpSession session) {
        Integer chats = (Integer) session.getAttribute("chats");
        session.setAttribute("chats", chats == null ? 1 : chats + 1);
    }
}