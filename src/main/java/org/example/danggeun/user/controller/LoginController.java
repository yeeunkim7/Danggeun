package org.example.danggeun.user.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequiredArgsConstructor
@Slf4j
public class LoginController {

    @GetMapping("/login")
    public String loginPage(@RequestParam(value = "error", required = false) String error,
                            @RequestParam(value = "message", required = false) String message,
                            Model model) {

        log.info("로그인 페이지 접근 - error: {}, message: {}", error, message);

        if (error != null) {
            String errorMessage = message != null ? message : "아이디 또는 비밀번호가 올바르지 않습니다.";
            model.addAttribute("error", errorMessage);
            log.info("로그인 에러 메시지 설정: {}", errorMessage);
        }

        return "login/login";
    }
}