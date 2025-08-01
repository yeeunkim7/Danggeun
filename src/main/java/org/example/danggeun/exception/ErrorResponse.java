package org.example.danggeun.exception;

import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.Map;

@Getter
@Builder
public class ErrorResponse {
    private String code;
    private String message;
    private String path;
    private LocalDateTime timestamp;
    private Map<String, String> details; // Validation 오류 시 필드별 메시지
}
