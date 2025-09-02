package org.example.danggeun.user.controller;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.danggeun.user.dto.UserRegisterRequestDto;
import org.example.danggeun.user.service.UserService;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.context.HttpSessionSecurityContextRepository;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequiredArgsConstructor
@Slf4j
public class AuthController {

    private final UserService userService;
    private final AuthenticationManager authenticationManager;

    @GetMapping("/register")
    public String registerPage(Model model) {
        model.addAttribute("user", new UserRegisterRequestDto());
        return "login/register";
    }

    @PostMapping("/register")
    public String registerUser(@Valid @ModelAttribute("user") UserRegisterRequestDto dto,
                               BindingResult bindingResult,
                               Model model,
                               HttpServletRequest request,
                               RedirectAttributes redirectAttributes) {

        log.info("회원가입 요청 - Email: {}, Username: {}", dto.getEmail(), dto.getUsername());

        // 유효성 검사 실패시
        if (bindingResult.hasErrors()) {
            log.warn("회원가입 유효성 검사 실패: {}", bindingResult.getAllErrors());
            return "login/register";
        }

        // 비밀번호 일치 확인
        if (!dto.getPassword().equals(dto.getConfirmPassword())) {
            log.warn("비밀번호 불일치 - Email: {}", dto.getEmail());
            model.addAttribute("error", "비밀번호가 일치하지 않습니다.");
            return "login/register";
        }

        Long userId = null;
        try {
            // 회원가입 처리 (별도 트랜잭션)
            userId = userService.signUp(dto.getEmail(), dto.getPassword(), dto.getUsername(), dto.getPhone());
            log.info("회원가입 성공 - User ID: {}, Email: {}", userId, dto.getEmail());

        } catch (IllegalArgumentException e) {
            log.warn("회원가입 실패 - 사용자 입력 오류: {}", e.getMessage());
            model.addAttribute("error", e.getMessage());
            return "login/register";
        } catch (Exception e) {
            log.error("회원가입 처리 중 시스템 오류 발생", e);
            model.addAttribute("error", "회원가입 처리 중 오류가 발생했습니다. 잠시 후 다시 시도해주세요.");
            return "login/register";
        }

        // 회원가입이 성공한 경우에만 자동 로그인 시도
        if (userId != null) {
            try {
                // 자동 로그인 시도 (별도 처리, 실패해도 회원가입은 완료됨)
                performAutoLogin(dto.getEmail(), dto.getPassword(), request);
                log.info("자동 로그인 성공: {}", dto.getEmail());
                redirectAttributes.addFlashAttribute("message", "회원가입이 완료되었습니다!");
                return "redirect:/";

            } catch (Exception e) {
                log.warn("자동 로그인 실패, 로그인 페이지로 이동: {}", e.getMessage());
                redirectAttributes.addFlashAttribute("message", "회원가입이 완료되었습니다. 로그인해주세요.");
                return "redirect:/login";
            }
        }

        return "redirect:/login";
    }

    /**
     * 자동 로그인 처리 (별도 메소드로 분리)
     */
    private void performAutoLogin(String email, String password, HttpServletRequest request) {
        var authToken = new UsernamePasswordAuthenticationToken(email, password);
        var authentication = authenticationManager.authenticate(authToken);
        SecurityContextHolder.getContext().setAuthentication(authentication);

        HttpSession session = request.getSession(true);
        session.setAttribute(
                HttpSessionSecurityContextRepository.SPRING_SECURITY_CONTEXT_KEY,
                SecurityContextHolder.getContext()
        );
    }

    // AJAX를 이용한 중복 검사 엔드포인트
    @GetMapping("/check-email")
    @ResponseBody
    public boolean checkEmailDuplicate(@RequestParam String email) {
        boolean isAvailable = !userService.existsByEmail(email);
        log.debug("이메일 중복 검사 - {}: {}", email, isAvailable ? "사용가능" : "중복");
        return isAvailable;
    }

    @GetMapping("/check-username")
    @ResponseBody
    public boolean checkUsernameDuplicate(@RequestParam String username) {
        boolean isAvailable = !userService.existsByUsername(username);
        log.debug("사용자명 중복 검사 - {}: {}", username, isAvailable ? "사용가능" : "중복");
        return isAvailable;
    }
}