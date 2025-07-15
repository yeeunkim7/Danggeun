package org.example.danggeun.security;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.example.danggeun.user.dto.UserDTO;
import org.example.danggeun.user.entity.User;
import org.example.danggeun.user.repository.UserRepository;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;

@RequiredArgsConstructor
@Component
public class CustomLoginSuccessHandler implements AuthenticationSuccessHandler {

    private final UserRepository userRepository;

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request,
                                        HttpServletResponse response,
                                        Authentication authentication)
            throws IOException, ServletException {

        UserDetails userDetails = (UserDetails) authentication.getPrincipal();
        String username = userDetails.getUsername();

        // 실제 User 조회
        User user = userRepository.findByUsername(username).orElse(null);

        // 세션에 커스텀 DTO 저장
        if (user != null) {
            UserDTO userDTO = new UserDTO(user.getUsername(), user.getEmail());
            HttpSession session = request.getSession();
            session.setAttribute("loginUser", userDTO);
        }

        // 로그인 후 메인페이지로 이동
        response.sendRedirect("/main");
    }
}
