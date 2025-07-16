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
import org.springframework.security.oauth2.core.user.OAuth2User;
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

        Object principal = authentication.getPrincipal();
        HttpSession session = request.getSession();

        if (principal instanceof OAuth2User oAuth2User) {
            String email = oAuth2User.getAttribute("email");
            User user = userRepository.findByEmail(email).orElse(null);

            if (user != null) {
                UserDTO userDTO = new UserDTO(user.getUsername(), user.getEmail());
                session.setAttribute("loginUser", userDTO);
            }

        } else if (principal instanceof UserDetails userDetails) {
            String username = userDetails.getUsername();
            User user = userRepository.findByEmail(username).orElse(null);

            if (user != null) {
                UserDTO userDTO = new UserDTO(user.getUsername(), user.getEmail());
                session.setAttribute("loginUser", userDTO);
            }
        }

        response.sendRedirect("/");
    }

}
