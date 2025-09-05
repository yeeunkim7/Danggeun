package org.example.danggeun.trade.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.danggeun.category.dto.CategoryDto;
import org.example.danggeun.category.service.CategoryService;
import org.example.danggeun.common.Constants;
import org.example.danggeun.trade.dto.TradeCreateRequestDto;
import org.example.danggeun.trade.dto.TradeDetailResponseDto;
import org.example.danggeun.trade.dto.TradeListResponseDto;
import org.example.danggeun.trade.service.TradeService;
import org.example.danggeun.user.entity.User;
import org.example.danggeun.user.service.UserService;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Controller
@RequiredArgsConstructor
public class TradeController {

    private final TradeService tradeService;
    private final UserService userService;
    private final CategoryService categoryService;

    @GetMapping("/trade")
    public String listProducts(Model model) {
        log.info("=== TradeController.listProducts() 시작 ===");

        List<TradeListResponseDto> productList = tradeService.findAllProducts();
        log.info("컨트롤러에서 받은 상품 목록 크기: {}", productList.size());

        model.addAttribute("itemList", productList);
        log.info("=== TradeController.listProducts() 완료 ===");

        return "trade/trade";
    }

    @GetMapping("/write")
    public String showWriteForm(Authentication authentication, Model model) {
        log.info("=== GET /write 요청 ===");
        log.info("Authentication 객체: {}", authentication != null ? "존재" : "null");

        if (authentication != null) {
            log.info("인증된 사용자: {}", authentication.getName());
        }

        // 임시로 인증 체크 제거 (디버깅용)
        String loginEmail = "test@example.com"; // 임시 이메일
        if (authentication != null) {
            try {
                loginEmail = getLoginEmail(authentication);
            } catch (Exception e) {
                log.warn("인증 정보 추출 실패, 임시 이메일 사용: {}", e.getMessage());
            }
        }

        model.addAttribute("loginEmail", loginEmail);
        model.addAttribute("product", new TradeCreateRequestDto());

        List<CategoryDto> categories = categoryService.findAll().stream()
                .map(c -> new CategoryDto(c.getId(), c.getName()))
                .collect(Collectors.toList());
        model.addAttribute("categories", categories);
        log.info("카테고리 수: {}", categories.size());

        return "write/write";
    }

    @PostMapping("/write")
    public String createProduct(
            @ModelAttribute TradeCreateRequestDto product,
            Authentication authentication,
            RedirectAttributes redirectAttributes
    ) {
        log.info("=== POST /write 요청 도달! ===");
        log.info("제품 제목: {}", product.getTitle());
        log.info("제품 가격: {}", product.getPrice());
        log.info("제품 설명: {}", product.getDetail());
        log.info("거래 장소: {}", product.getAddress());
        log.info("카테고리 ID: {}", product.getCategoryId());
        log.info("이미지: {}", product.getImage() != null ? product.getImage().getOriginalFilename() : "없음");

        try {
            // 임시로 고정 사용자 ID 사용 (디버깅용)
            Long userId = 1L;  // 실제로는 인증된 사용자 ID를 사용해야 함

            if (authentication != null) {
                try {
                    String loginEmail = getLoginEmail(authentication);
                    log.info("로그인 사용자: {}", loginEmail);

                    User loginUser = userService.findByEmail(loginEmail)
                            .orElseThrow(() -> new IllegalStateException("사용자를 찾을 수 없습니다: " + loginEmail));
                    userId = loginUser.getId();
                    log.info("사용자 조회 성공: ID={}", userId);
                } catch (Exception e) {
                    log.warn("사용자 인증 실패, 기본 사용자 ID 사용: {}", e.getMessage());
                }
            }

            // 상품 생성
            Long productId = tradeService.createProduct(product, userId);
            log.info("상품 등록 성공! 생성된 상품 ID: {}", productId);

            redirectAttributes.addFlashAttribute("success", "상품이 성공적으로 등록되었습니다.");
            return "redirect:" + Constants.TRADE_URL_PREFIX + "/" + productId;

        } catch (IOException e) {
            log.error("이미지 업로드 실패", e);
            redirectAttributes.addFlashAttribute("error", "이미지 업로드에 실패했습니다.");
            return "redirect:/write";
        } catch (IllegalArgumentException e) {
            log.error("데이터 유효성 검사 실패: {}", e.getMessage());
            redirectAttributes.addFlashAttribute("error", e.getMessage());
            return "redirect:/write";
        } catch (Exception e) {
            log.error("상품 등록 실패 - 예상치 못한 오류", e);
            redirectAttributes.addFlashAttribute("error", "상품 등록에 실패했습니다. 오류: " + e.getMessage());
            return "redirect:/write";
        }
    }

    @GetMapping("/trade/{productId}")
    public String showProductDetail(@PathVariable Long productId, Model model) {
        log.info("상품 상세 조회 - ID: {}", productId);

        try {
            TradeDetailResponseDto productDto = tradeService.findTradeById(productId);
            model.addAttribute("product", productDto);
            log.info("상품 상세 조회 성공 - 제목: {}", productDto.getTitle());
            return "trade/tradePost";
        } catch (IllegalArgumentException e) {
            log.warn("존재하지 않는 상품 조회 시도: {}", productId);
            model.addAttribute("error", "존재하지 않는 상품입니다.");
            return "error/404";
        } catch (Exception e) {
            log.error("상품 상세 조회 실패: {}", productId, e);
            model.addAttribute("error", "상품 정보를 불러오는데 실패했습니다.");
            return "error/500";
        }
    }

    @GetMapping("/trade/update/{productId}")
    public String showUpdateForm(@PathVariable Long productId, Model model) {
        try {
            TradeDetailResponseDto dto = tradeService.findTradeById(productId);
            model.addAttribute("product", dto);

            List<CategoryDto> categories = categoryService.findAll().stream()
                    .map(c -> new CategoryDto(c.getId(), c.getName()))
                    .collect(Collectors.toList());
            model.addAttribute("categories", categories);

            return "trade/updateTradePost";
        } catch (IllegalArgumentException e) {
            log.warn("존재하지 않는 상품 수정 시도: {}", productId);
            model.addAttribute("error", "존재하지 않는 상품입니다.");
            return "error/404";
        } catch (Exception e) {
            log.error("상품 수정 폼 로드 실패: {}", productId, e);
            model.addAttribute("error", "상품 정보를 불러오는데 실패했습니다.");
            return "error/500";
        }
    }

    @PostMapping("/trade/update/{productId}")
    public String updateProduct(
            @PathVariable Long productId,
            @ModelAttribute TradeCreateRequestDto updateDto,
            Authentication authentication,
            RedirectAttributes redirectAttributes
    ) {
        log.info("=== 상품 수정 시작 - ID: {} ===", productId);

        try {
            // 임시로 고정 사용자 ID 사용
            Long userId = 1L;

            if (authentication != null) {
                try {
                    String loginEmail = getLoginEmail(authentication);
                    User loginUser = userService.findByEmail(loginEmail)
                            .orElseThrow(() -> new IllegalStateException("사용자를 찾을 수 없습니다: " + loginEmail));
                    userId = loginUser.getId();
                } catch (Exception e) {
                    log.warn("사용자 인증 실패, 기본 사용자 ID 사용: {}", e.getMessage());
                }
            }

            tradeService.updateProduct(productId, updateDto, userId);
            log.info("상품 수정 성공 - ID: {}", productId);

            redirectAttributes.addFlashAttribute("success", "상품이 성공적으로 수정되었습니다.");
            return "redirect:" + Constants.TRADE_URL_PREFIX + "/" + productId;

        } catch (IOException e) {
            log.error("상품 수정 중 이미지 업로드 실패", e);
            redirectAttributes.addFlashAttribute("error", "이미지 업로드에 실패했습니다.");
            return "redirect:/trade/update/" + productId;
        } catch (Exception e) {
            log.error("상품 수정 실패", e);
            redirectAttributes.addFlashAttribute("error", "상품 수정에 실패했습니다: " + e.getMessage());
            return "redirect:/trade/update/" + productId;
        }
    }

    /**
     * 로그인 이메일 추출 헬퍼 메소드
     */
    private String getLoginEmail(Authentication authentication) {
        if (authentication == null) {
            throw new IllegalStateException("로그인이 필요합니다.");
        }

        Object principal = authentication.getPrincipal();
        log.info("Authentication principal 타입: {}", principal.getClass().getSimpleName());

        if (principal instanceof OAuth2User oauth2User) {
            String email = oauth2User.getAttribute("email");
            log.info("OAuth2 사용자 이메일: {}", email);
            return email;
        } else if (principal instanceof UserDetails userDetails) {
            String username = userDetails.getUsername();
            log.info("UserDetails 사용자명: {}", username);
            return username;
        } else if ("anonymousUser".equals(principal.toString())) {
            throw new IllegalStateException("로그인이 필요합니다.");
        } else {
            log.error("알 수 없는 인증 타입: {}", principal.getClass());
            throw new IllegalStateException("알 수 없는 인증 타입: " + principal.getClass());
        }
    }

    /**
     * 전역 예외 처리
     */
    @ExceptionHandler(IllegalArgumentException.class)
    public String handleIllegalArgument(IllegalArgumentException e, Model model) {
        log.warn("잘못된 요청: {}", e.getMessage());
        model.addAttribute("error", e.getMessage());
        return "error/400";
    }

    @ExceptionHandler(IllegalStateException.class)
    public String handleIllegalState(IllegalStateException e, Model model) {
        log.warn("잘못된 상태: {}", e.getMessage());
        model.addAttribute("error", e.getMessage());
        return "error/401";
    }
}