package org.example.danggeun.user.controller;

import org.example.danggeun.user.entity.User;
import org.example.danggeun.user.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class AuthController {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Autowired
    public AuthController(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @GetMapping("/register")
    public String registerPage() {
        return "login/register";
    }

    @PostMapping("/register")
    public String registerUser(@RequestParam String username,
                               @RequestParam String password,
                               @RequestParam String confirmPassword,
                               Model model) {
        if (!password.equals(confirmPassword)) {
            model.addAttribute("error", "비밀번호가 일치하지 않습니다.");
            return "login/register";
        }

        if (userRepository.findByUsername(username).isPresent()) {
            model.addAttribute("error", "이미 존재하는 아이디입니다.");
            return "login/register";
        }

        User user = new User();
        user.setUsername(username);
        user.setPassword(passwordEncoder.encode(password));
        userRepository.save(user);

        return "redirect:/login";
    }
}
