package org.example.danggeun.s3.service;

import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Collections;
import java.util.List;

@Profile("local")
@Service
public class LocalS3Service implements S3Service {

    @Override
    public String uploadFile(MultipartFile file) throws IOException {
        // 로컬 환경에서는 파일을 실제로 업로드하지 않으므로 임시 경로 반환
        return "local-fake-path/" + file.getOriginalFilename();
    }

    @Override
    public String getFileUrl(String fileName) {
        // 로컬 환경에서는 접근 URL도 임시로 반환
        return "http://localhost:8080/files/" + fileName;
    }

    @Override
    public void deleteFile(String fileName) {
        // 로컬에서는 삭제 로직 생략
        System.out.println("파일 삭제 요청 받음 (실제로 삭제하지 않음): " + fileName);
    }

    @Override
    public List<String> listFiles() {
        // 로컬 테스트용으로 빈 리스트 반환
        return Collections.emptyList();
    }
}
