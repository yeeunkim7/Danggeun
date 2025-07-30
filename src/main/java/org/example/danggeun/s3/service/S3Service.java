package org.example.danggeun.s3.service;

import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

public interface S3Service {
    String uploadFile(MultipartFile file) throws IOException;
    String getFileUrl(String fileName);
    void deleteFile(String fileName);
    List<String> listFiles();
}
