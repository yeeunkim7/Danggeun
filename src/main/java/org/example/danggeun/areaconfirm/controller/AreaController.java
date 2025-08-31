package org.example.danggeun.areaconfirm.controller;

import lombok.RequiredArgsConstructor;
import org.example.danggeun.areaconfirm.dto.AreaRequestDto;
import org.example.danggeun.areaconfirm.service.AreaService;
import org.example.danggeun.user.entity.User;
import org.example.danggeun.user.service.UserService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequiredArgsConstructor
public class AreaController {

    private final AreaService areaService;
    private final UserService userService;

    @Value("${google.maps.api-key}")
    private String googleMapsApiKey;

    @GetMapping("/areaConfirm")
    public String showAreaConfirm(Authentication authentication, Model model) {
        try {
            String loginEmail = getLoginEmail(authentication);
            User user = userService.findByEmail(loginEmail)
                    .orElseThrow(() -> new IllegalStateException("로그인된 사용자를 찾을 수 없습니다."));

            model.addAttribute("user", user);
            model.addAttribute("googleApiKey", googleMapsApiKey); // Google Maps API 키 추가
            return "areaConfirm/areaConfirm";

        } catch (Exception e) {
            model.addAttribute("error", "동네인증 페이지 로딩 중 오류가 발생했습니다.");
            return "error/500";
        }
    }

    @RestController
    @RequestMapping("/api/area")
    @RequiredArgsConstructor
    public static class AreaApiController {

        private final AreaService areaService;

        @PostMapping("/confirm")
        public String confirmArea(@RequestBody AreaRequestDto dto) {
            return areaService.saveArea(dto);
        }

        @GetMapping("/me")
        public AreaRequestDto getMyArea(@RequestParam("userId") Long userId) {
            return areaService.findAreaByUserId(userId);
        }
    }

    private String getLoginEmail(Authentication authentication) {
        Object principal = authentication.getPrincipal();
        if (principal instanceof OAuth2User oauth2User) {
            return oauth2User.getAttribute("email");
        } else if (principal instanceof UserDetails userDetails) {
            return userDetails.getUsername();
        } else {
            throw new IllegalStateException("알 수 없는 인증 타입: " + principal.getClass());
        }
    }
}