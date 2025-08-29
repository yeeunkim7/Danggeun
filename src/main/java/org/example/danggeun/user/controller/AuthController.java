package org.example.danggeun.user.controller;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.example.danggeun.user.dto.UserRegisterRequestDto;
import org.example.danggeun.user.service.UserService;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.context.HttpSessionSecurityContextRepository;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequiredArgsConstructor
public class AuthController {

    private final UserService userService;
    private final AuthenticationManager authenticationManager;

    @GetMapping("/register")
    public String registerPage(Model model) {
        model.addAttribute("user", new UserRegisterRequestDto());
        return "login/register";
    }

    @PostMapping("/register")
    public String registerUser(@ModelAttribute("user") UserRegisterRequestDto dto,
                               Model model,
                               HttpServletRequest request) {
        if (!dto.getPassword().equals(dto.getConfirmPassword())) {
            model.addAttribute("error", "비밀번호가 일치하지 않습니다.");
            return "login/register";
        }

        try {
            userService.signUp(dto.getEmail(), dto.getPassword(), dto.getUsername(), dto.getPhone());
        } catch (IllegalArgumentException e) {
            model.addAttribute("error", e.getMessage());
            return "login/register";
        } catch (Exception e) {
            model.addAttribute("error", "회원가입 처리 중 오류가 발생했습니다.");
            return "login/register";
        }

        // 회원가입 직후 자동 로그인
        var authToken = new UsernamePasswordAuthenticationToken(dto.getEmail(), dto.getPassword());
        var authentication = authenticationManager.authenticate(authToken);
        SecurityContextHolder.getContext().setAuthentication(authentication);

        HttpSession session = request.getSession(true);
        session.setAttribute(
                HttpSessionSecurityContextRepository.SPRING_SECURITY_CONTEXT_KEY,
                SecurityContextHolder.getContext()
        );

        return "redirect:/";
    }
}
