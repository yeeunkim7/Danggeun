package org.example.danggeun.trade.service;

import java.io.File;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.UUID;
import jakarta.servlet.http.HttpSession;
import org.example.danggeun.trade.dto.TradeDto;
import org.example.danggeun.write.entity.Write;
import org.example.danggeun.write.repository.WriteRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Service
public class TradeService {

    private static final Logger log = LoggerFactory.getLogger(TradeService.class);

    private final WriteRepository writeRepository;

    public TradeService(WriteRepository writeRepository) {
        this.writeRepository = writeRepository;
    }

    public Long submitAndStoreInSession(TradeDto dto, MultipartFile imageFile, HttpSession session) {
        if (imageFile == null || imageFile.isEmpty()) {
            throw new IllegalArgumentException("이미지는 필수입니다.");
        }

        String originalFilename = imageFile.getOriginalFilename();
        String storedFileName = UUID.randomUUID() + "_" + originalFilename;

        // 클래스패스 기준 static/uploads 경로
        String uploadPath = new File("target/classes/static/uploads").getAbsolutePath();
        File saveFile = new File(uploadPath, storedFileName);

        try {
            saveFile.getParentFile().mkdirs();
            imageFile.transferTo(saveFile);
        } catch (IOException e) {
            log.error("파일 저장 실패", e);
            throw new RuntimeException("파일 저장 실패", e);
        }

        String imageUrl = "/uploads/" + storedFileName;

        Write write = Write.builder()
                .productNm(dto.getTitle())
                .productPrice(dto.getProductPrice())
                .productDetail(dto.getProductDetail())
                .address(dto.getAddress())
                .productImg("/uploads/" + storedFileName)
                .productCreatedAt(LocalDateTime.now())
                .userId2(1L)
                .categoryId(1L)
                .build();


        Write saved = writeRepository.save(write);
        Long savedId = saved.getProductId();

        session.setAttribute("postId", savedId);

        System.out.println("저장된 product_id: " + savedId);

        return savedId;
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

    public void updatePost(Long id, TradeDto dto, MultipartFile file) {
        Write post = writeRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("수정할 게시글이 없습니다."));

        post.setProductNm(dto.getTitle());
        post.setProductPrice(dto.getProductPrice());
        post.setProductDetail(dto.getProductDetail());
        post.setAddress(dto.getAddress());

        if (file != null && !file.isEmpty()) {
            String filename = UUID.randomUUID() + "_" + file.getOriginalFilename();
            String uploadPath = new File("target/classes/static/uploads").getAbsolutePath();
            File saveFile = new File(uploadPath, filename);
            try {
                saveFile.getParentFile().mkdirs();
                file.transferTo(saveFile);
                post.setProductImg("/uploads/" + filename);
            } catch (IOException e) {
                throw new RuntimeException("이미지 업로드 실패", e);
            }
        }

        writeRepository.save(post);
    }

    public void increaseChatInSession(HttpSession session) {
        Integer chats = (Integer) session.getAttribute("chats");
        session.setAttribute("chats", chats == null ? 1 : chats + 1);
    }
}