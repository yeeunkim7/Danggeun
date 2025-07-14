package org.example.danggeun.security;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Slf4j
@Component
public class CustomLoginFailureHandler implements AuthenticationFailureHandler {

    @Override
    public void onAuthenticationFailure(HttpServletRequest request,
                                        HttpServletResponse response,
                                        AuthenticationException exception)
            throws IOException, ServletException {

        // 콘솔 또는 로그에 로그인 실패 내용 기록
        log.warn("로그인 실패: {}", exception.getMessage());

        // 로그인 페이지로 실패 사유를 쿼리 파라미터로 넘김
        response.sendRedirect("/login?error=invalid");
    }
}
