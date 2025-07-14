package org.example.danggeun.common; // 공통 유틸리티 패키지에 생성

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;
import java.io.File;
import java.io.IOException;
import java.util.UUID;

@Component // 스프링 빈으로 등록
public class FileStore {

    @Value("${file.upload-dir}")
    private String uploadDir;

    public String storeFile(MultipartFile multipartFile) throws IOException {
        if (multipartFile == null || multipartFile.isEmpty()) {
            return null; // 파일이 없으면 null 반환
        }

        String originalFilename = multipartFile.getOriginalFilename();
        String storedFileName = UUID.randomUUID() + "_" + originalFilename;
        String fileUrl = "/uploads/" + storedFileName; // 웹에서 접근할 경로

        File dest = new File(uploadDir + File.separator + storedFileName);
        dest.getParentFile().mkdirs(); // 디렉토리가 없으면 생성
        multipartFile.transferTo(dest);

        return fileUrl;
    }
}