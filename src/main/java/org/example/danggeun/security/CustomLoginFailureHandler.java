package org.example.danggeun.security;

import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.LockedException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.stereotype.Component;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

@Component
@Slf4j
public class CustomLoginFailureHandler implements AuthenticationFailureHandler {

    @Override
    public void onAuthenticationFailure(HttpServletRequest request,
                                        HttpServletResponse response,
                                        AuthenticationException exception) throws IOException, ServletException {

        String errorMessage = "로그인에 실패했습니다.";
        String email = request.getParameter("email");

        log.warn("로그인 실패 - Email: {}, 에러: {}", email, exception.getMessage());

        // 예외 타입에 따른 구체적인 에러 메시지 설정
        if (exception instanceof BadCredentialsException) {
            errorMessage = "아이디 또는 비밀번호가 올바르지 않습니다.";
            log.warn("잘못된 인증 정보 - Email: {}", email);
        } else if (exception instanceof UsernameNotFoundException) {
            errorMessage = "존재하지 않는 계정입니다.";
            log.warn("존재하지 않는 계정으로 로그인 시도 - Email: {}", email);
        } else if (exception instanceof DisabledException) {
            errorMessage = "비활성화된 계정입니다.";
            log.warn("비활성화된 계정 로그인 시도 - Email: {}", email);
        } else if (exception instanceof LockedException) {
            errorMessage = "잠긴 계정입니다.";
            log.warn("잠긴 계정 로그인 시도 - Email: {}", email);
        } else {
            log.error("예상치 못한 로그인 오류 - Email: {}, Exception: {}", email, exception.getClass().getSimpleName(), exception);
        }

        // 에러 메시지를 URL 파라미터로 전달
        String encodedErrorMessage = URLEncoder.encode(errorMessage, StandardCharsets.UTF_8);

        // 리다이렉트 URL에 에러 메시지 포함
        response.sendRedirect("/login?error=true&message=" + encodedErrorMessage);
    }
}