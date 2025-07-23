package org.example.danggeun.common;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;
import java.io.File;
import java.io.IOException;
import java.util.UUID;

@Component
public class FileStore {

    @Value("${file.upload-dir}")
    private String uploadDir;

    public String storeFile(MultipartFile multipartFile) throws IOException {
        if (multipartFile == null || multipartFile.isEmpty()) {
            return null;
        }

        String originalFilename = multipartFile.getOriginalFilename();
        String storedFileName = UUID.randomUUID() + "_" + originalFilename;
        String fileUrl = "/uploads/" + storedFileName;

        File dest = new File(uploadDir + File.separator + storedFileName);
        dest.getParentFile().mkdirs();
        multipartFile.transferTo(dest);

        return fileUrl;
    }
}