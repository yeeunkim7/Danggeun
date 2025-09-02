package org.example.danggeun.exception;

import jakarta.servlet.http.HttpServletRequest;
import lombok.AllArgsConstructor;
import lombok.Data;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import org.springframework.web.servlet.resource.NoResourceFoundException;

import java.time.LocalDateTime;

@RestControllerAdvice
public class GlobalExceptionHandler {

    private static final Logger logger = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    @ExceptionHandler(NoResourceFoundException.class)
    public ResponseEntity<?> handleNoResourceFound(NoResourceFoundException ex, HttpServletRequest request) {
        String requestURI = request.getRequestURI();

        // favicon이나 기타 브라우저 요청은 조용히 404 처리
        if (requestURI.contains("favicon") ||
                requestURI.contains(".well-known") ||
                requestURI.contains("robots.txt")) {
            return ResponseEntity.notFound().build();
        }

        // 이미지나 CSS, JS 파일은 404로 처리
        if (requestURI.contains("/img/") ||
                requestURI.contains("/css/") ||
                requestURI.contains("/js/") ||
                requestURI.endsWith(".css") ||
                requestURI.endsWith(".js") ||
                requestURI.endsWith(".png") ||
                requestURI.endsWith(".jpg") ||
                requestURI.endsWith(".jpeg")) {
            logger.warn("정적 리소스를 찾을 수 없습니다: {}", requestURI);
            return ResponseEntity.notFound().build();
        }

        // 나머지는 기존대로 처리
        logger.error("리소스를 찾을 수 없습니다: {}", requestURI);
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(new ErrorResponse("RESOURCE_NOT_FOUND", "요청한 리소스를 찾을 수 없습니다.", LocalDateTime.now()));
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleGenericException(Exception ex) {
        // NoResourceFoundException은 별도 처리하므로 제외
        if (ex instanceof NoResourceFoundException) {
            return (ResponseEntity<ErrorResponse>) handleNoResourceFound((NoResourceFoundException) ex,
                    ((ServletRequestAttributes) RequestContextHolder.currentRequestAttributes()).getRequest());
        }

        logger.error("예상치 못한 오류 발생", ex);
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(new ErrorResponse("UNKNOWN_ERROR", "예상치 못한 오류가 발생했습니다.", LocalDateTime.now()));
    }

    @Data
    @AllArgsConstructor
    public static class ErrorResponse {
        private String code;
        private String message;
        private LocalDateTime timestamp;
    }
}