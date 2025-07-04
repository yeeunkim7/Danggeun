package org.example.danggeun.service;

import java.io.File;
import java.io.IOException;
import jakarta.servlet.http.HttpSession;
import org.example.danggeun.controller.Dto.TradeDto;
import org.example.danggeun.controller.Dto.TradeForm;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class TradeService {

    // application.properties 또는 application.yml에서 읽을 수 있도록 경로 외부 설정 권장
    @Value("${file.upload-dir}")  // 예: file.upload-dir=/Users/user/uploads 또는 C:/uploads
    private String uploadDir;

    public void submitTrade(TradeForm form, HttpSession session) {
        String fileName = System.currentTimeMillis() + "_" + form.productImage().getOriginalFilename();

        // 업로드 디렉토리 설정
        File folder = new File(uploadDir);
        if (!folder.exists()) {
            boolean created = folder.mkdirs();
            if (!created) {
                throw new RuntimeException("업로드 폴더 생성 실패");
            }
        }

        File savedFile = new File(folder, fileName);

        try {
            form.productImage().transferTo(savedFile);
        } catch (IOException e) {
            e.printStackTrace();
            throw new RuntimeException("이미지 저장 실패", e);
        }

        // 세션에 데이터 저장
        session.setAttribute("title", form.title());
        session.setAttribute("productPrice", form.productPrice());
        session.setAttribute("productDetail", form.productDetail());
        session.setAttribute("address", form.address());
        session.setAttribute("imageUrl", "/uploads/" + fileName);
        session.setAttribute("views", 1);
        session.setAttribute("chats", 0);
    }
    public TradeDto getPost(HttpSession session) {
        TradeDto dto = new TradeDto();

        // 세션에서 필요한 속성들을 꺼내서 dto에 세팅
        dto.setTitle((String) session.getAttribute("title"));
        dto.setProductPrice((String) session.getAttribute("productPrice"));
        dto.setProductDetail((String) session.getAttribute("productDetail"));
        dto.setAddress((String) session.getAttribute("address"));
        dto.setImageUrl((String) session.getAttribute("imageUrl"));
        dto.setViews((Integer) session.getAttribute("views"));
        dto.setChats((Integer) session.getAttribute("chats"));

        return dto;
    }

    // 채팅 수 증가 예시
    public void increaseChat(HttpSession session) {
        Integer chats = (Integer) session.getAttribute("chats");
        if (chats == null) chats = 0;
        session.setAttribute("chats", chats + 1);
    }
}