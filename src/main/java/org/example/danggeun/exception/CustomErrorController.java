package org.example.danggeun.exception;

import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.web.servlet.error.ErrorController;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import java.time.LocalDateTime;

@Slf4j
@Controller
public class CustomErrorController implements ErrorController {

    @RequestMapping("/error")
    public Object handleError(HttpServletRequest request) {
        Object statusObj = request.getAttribute(RequestDispatcher.ERROR_STATUS_CODE);
        int statusCode = statusObj != null ? Integer.parseInt(statusObj.toString()) : 500;

        String accept = request.getHeader("Accept");

        if (accept != null && accept.contains("text/html")) {
            // HTML 응답 요청이면 error.html 렌더링
            request.setAttribute("status", String.valueOf(statusCode));
            request.setAttribute("message", getMessageByStatus(statusCode));
            return "error";  // templates/error.html
        } else {
            // API 요청이면 JSON 응답
            ErrorResponse response = ErrorResponse.builder()
                    .code(HttpStatus.resolve(statusCode) != null
                            ? HttpStatus.resolve(statusCode).name()
                            : "ERROR")
                    .message(getMessageByStatus(statusCode))
                    .path((String) request.getAttribute(RequestDispatcher.ERROR_REQUEST_URI))
                    .timestamp(LocalDateTime.now())
                    .build();

            return ResponseEntity.status(statusCode).body(response);
        }
    }

    private String getMessageByStatus(int status) {
        return switch (status) {
            case 400 -> "잘못된 요청입니다.";
            case 401 -> "인증이 필요합니다.";
            case 403 -> "접근이 거부되었습니다.";
            case 404 -> "요청하신 페이지를 찾을 수 없습니다.";
            case 500 -> "서버에 오류가 발생했습니다.";
            default -> "알 수 없는 오류가 발생했습니다.";
        };
    }
}
