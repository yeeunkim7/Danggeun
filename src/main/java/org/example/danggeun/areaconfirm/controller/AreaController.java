package org.example.danggeun.areaconfirm.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.danggeun.areaconfirm.dto.AreaRequestDto;
import org.example.danggeun.areaconfirm.dto.AreaResponseDto;
import org.example.danggeun.areaconfirm.service.AreaService;
import org.example.danggeun.user.entity.User;
import org.example.danggeun.user.service.UserService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@Slf4j
@Controller
@RequiredArgsConstructor
public class AreaController {

    private final AreaService areaService;
    private final UserService userService;

    @Value("${google.maps.api.key:}")
    private String googleMapsApiKey;

    /**
     * 동네인증 페이지 표시
     */
    @GetMapping("/areaConfirm")
    public String showAreaConfirm(Authentication authentication, Model model) {
        try {
            log.info("동네인증 페이지 요청");

            // 로그인 확인
            if (authentication == null || !authentication.isAuthenticated()) {
                log.warn("로그인되지 않은 사용자의 접근");
                return "redirect:/login";
            }

            String loginEmail = authentication.getName();
            log.info("로그인된 사용자: {}", loginEmail);

            User user = userService.findByEmail(loginEmail)
                    .orElseThrow(() -> new IllegalStateException("로그인된 사용자를 찾을 수 없습니다."));

            model.addAttribute("user", user);

            // Google Maps API 키 추가
            if (googleMapsApiKey != null && !googleMapsApiKey.trim().isEmpty()) {
                model.addAttribute("googleApiKey", googleMapsApiKey);
                log.info("Google Maps API 키 설정 완료");
            } else {
                log.warn("Google Maps API 키가 설정되지 않았습니다.");
            }

            // 기존 동네 인증 정보가 있는지 확인
            try {
                if (areaService.hasAreaConfirmation(user.getId())) {
                    AreaResponseDto areaInfo = areaService.findAreaByUserId(user.getId());
                    model.addAttribute("existingArea", areaInfo);
                    log.info("기존 동네 인증 정보 로드 완료. UserId: {}, Address: {}",
                            user.getId(), areaInfo.getAddress());
                }
            } catch (Exception e) {
                log.warn("기존 동네 인증 정보 로드 실패: {}", e.getMessage());
                // 기존 정보 로드 실패는 무시하고 계속 진행
            }

            return "areaConfirm/areaConfirm";

        } catch (Exception e) {
            log.error("동네인증 페이지 로딩 실패", e);
            model.addAttribute("error", "동네인증 페이지 로딩 중 오류가 발생했습니다: " + e.getMessage());
            return "error/500";
        }
    }

    /**
     * 동네 인증 처리
     */
    @PostMapping("/area/confirm")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> confirmArea(@RequestBody AreaRequestDto areaRequestDto,
                                                           Authentication authentication) {
        try {
            log.info("동네 인증 요청 받음");

            // 입력 검증
            if (areaRequestDto == null) {
                return ResponseEntity.badRequest()
                        .body(createErrorResponse("요청 데이터가 없습니다."));
            }

            String loginEmail = getLoginEmail(authentication);
            User user = userService.findByEmail(loginEmail)
                    .orElseThrow(() -> new IllegalStateException("로그인된 사용자를 찾을 수 없습니다."));

            areaRequestDto.setUserId(user.getId());

            // 수동 검증 수행
            areaRequestDto.validate();

            AreaResponseDto result = areaService.saveArea(areaRequestDto);

            log.info("동네 인증 완료. UserId: {}, Address: {}", user.getId(), result.getAddress());

            return ResponseEntity.ok()
                    .body(createSuccessResponse("동네 인증이 완료되었습니다.", result));

        } catch (IllegalArgumentException e) {
            log.warn("동네 인증 실패 - 잘못된 요청: {}", e.getMessage());
            return ResponseEntity.badRequest()
                    .body(createErrorResponse(e.getMessage()));
        } catch (IllegalStateException e) {
            log.warn("동네 인증 실패 - 인증 오류: {}", e.getMessage());
            return ResponseEntity.status(401)
                    .body(createErrorResponse(e.getMessage()));
        } catch (Exception e) {
            log.error("동네 인증 중 오류 발생", e);
            return ResponseEntity.internalServerError()
                    .body(createErrorResponse("동네 인증 중 오류가 발생했습니다."));
        }
    }

    /**
     * 동네 인증 정보 조회
     */
    @GetMapping("/area/info")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> getAreaInfo(Authentication authentication) {
        try {
            String loginEmail = getLoginEmail(authentication);
            User user = userService.findByEmail(loginEmail)
                    .orElseThrow(() -> new IllegalStateException("로그인된 사용자를 찾을 수 없습니다."));

            if (!areaService.hasAreaConfirmation(user.getId())) {
                Map<String, Object> response = createSuccessResponse("동네 인증 정보가 없습니다.", null);
                response.put("hasAreaConfirmation", false);
                return ResponseEntity.ok().body(response);
            }

            AreaResponseDto areaInfo = areaService.findAreaByUserId(user.getId());
            Map<String, Object> response = createSuccessResponse(null, areaInfo);
            response.put("hasAreaConfirmation", true);

            return ResponseEntity.ok().body(response);

        } catch (IllegalStateException e) {
            log.warn("동네 인증 정보 조회 실패 - 인증 오류: {}", e.getMessage());
            return ResponseEntity.status(401)
                    .body(createErrorResponse(e.getMessage()));
        } catch (Exception e) {
            log.error("동네 인증 정보 조회 중 오류 발생", e);
            return ResponseEntity.internalServerError()
                    .body(createErrorResponse("동네 인증 정보 조회 중 오류가 발생했습니다."));
        }
    }

    /**
     * 테스트용 매핑
     */
    @GetMapping("/test")
    @ResponseBody
    public String test() {
        return "AreaController가 정상 작동합니다!";
    }

    /**
     * 현재 로그인한 사용자의 이메일을 가져옵니다.
     */
    private String getLoginEmail(Authentication authentication) {
        if (authentication == null || !authentication.isAuthenticated()) {
            throw new IllegalStateException("로그인이 필요합니다.");
        }

        String email = authentication.getName();
        if (email == null || email.trim().isEmpty()) {
            throw new IllegalStateException("사용자 정보를 찾을 수 없습니다.");
        }

        return email;
    }

    /**
     * 성공 응답 생성 헬퍼 메서드
     */
    private Map<String, Object> createSuccessResponse(String message, Object data) {
        Map<String, Object> response = new HashMap<>();
        response.put("success", true);
        if (message != null && !message.trim().isEmpty()) {
            response.put("message", message);
        }
        if (data != null) {
            response.put("data", data);
        }
        return response;
    }

    /**
     * 에러 응답 생성 헬퍼 메서드
     */
    private Map<String, Object> createErrorResponse(String message) {
        Map<String, Object> response = new HashMap<>();
        response.put("success", false);
        response.put("message", message != null ? message : "알 수 없는 오류가 발생했습니다.");
        return response;
    }
}