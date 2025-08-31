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
        List<TradeListResponseDto> productList = tradeService.findAllProducts();
        model.addAttribute("itemList", productList);
        return "trade/trade";
    }

    @GetMapping("/write")
    public String showWriteForm(Authentication authentication, Model model) {
        String loginEmail = getLoginEmail(authentication);
        model.addAttribute("loginEmail", loginEmail);
        model.addAttribute("product", new TradeCreateRequestDto());

        List<CategoryDto> categories = categoryService.findAll().stream()
                .map(c -> new CategoryDto(c.getId(), c.getName()))
                .collect(Collectors.toList());
        model.addAttribute("categories", categories);

        return "write/write";
    }

    @PostMapping("/write")
    public String createProduct(
            @ModelAttribute TradeCreateRequestDto product,
            Authentication authentication,
            RedirectAttributes redirectAttributes
    ) {
        try {
            String loginEmail = getLoginEmail(authentication);

            // 수정: Optional 처리
            User loginUser = userService.findByEmail(loginEmail)
                    .orElseThrow(() -> new IllegalStateException("존재하지 않는 사용자: " + loginEmail));

            if (product.getImage() == null || product.getImage().isEmpty()) {
                throw new IllegalArgumentException("이미지 파일이 없습니다.");
            }

            Long productId = tradeService.createProduct(product, loginUser.getId());
            return "redirect:" + Constants.TRADE_URL_PREFIX + "/" + productId;

        } catch (IOException e) {
            log.error("상품 등록 중 이미지 업로드 실패", e);
            redirectAttributes.addFlashAttribute("error", "이미지 업로드에 실패했습니다.");
            return "redirect:/write";
        } catch (Exception e) {
            log.error("상품 등록 실패", e);
            redirectAttributes.addFlashAttribute("error", "상품 등록에 실패했습니다: " + e.getMessage());
            return "redirect:/write";
        }
    }

    @GetMapping("/trade/{productId}")
    public String showProductDetail(@PathVariable Long productId, Model model) {
        try {
            TradeDetailResponseDto productDto = tradeService.findTradeById(productId);
            model.addAttribute("product", productDto);
            return "trade/tradePost";
        } catch (IllegalArgumentException e) {
            log.warn("존재하지 않는 상품 조회 시도: {}", productId);
            model.addAttribute("error", "존재하지 않는 상품입니다.");
            return "error/404"; // 또는 "trade/trade"로 리다이렉트
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
            return "trade/updateTradePost";
        } catch (IllegalArgumentException e) {
            log.warn("존재하지 않는 상품 수정 시도: {}", productId);
            model.addAttribute("error", "존재하지 않는 상품입니다.");
            return "error/404";
        }
    }

    // 로그인 이메일 추출 헬퍼 메소드
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

    // 전역 예외 처리
    @ExceptionHandler(IllegalArgumentException.class)
    public String handleIllegalArgument(IllegalArgumentException e, Model model) {
        log.warn("잘못된 요청: {}", e.getMessage());
        model.addAttribute("error", e.getMessage());
        return "error/400";
    }
}