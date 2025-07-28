package org.example.danggeun;

import io.github.cdimascio.dotenv.Dotenv; // 추가
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class DanggeunApplication {

    public static void main(String[] args) {

        // 1. .env 파일 로드
        Dotenv dotenv = Dotenv.configure()
                .ignoreIfMissing()   // .env 파일 없어도 실행 에러 안 나게
                .load();

        // 2. 환경 변수들을 시스템 프로퍼티로 등록
        dotenv.entries().forEach(entry ->
                System.setProperty(entry.getKey(), entry.getValue())
        );

        // 3. Spring Boot 애플리케이션 실행
        SpringApplication.run(DanggeunApplication.class, args);
    }

}
