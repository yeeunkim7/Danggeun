package org.example.danggeun.trade.controller; // 패키지 경로를 product로 변경하는 것을 권장합니다.

import lombok.RequiredArgsConstructor;
import org.example.danggeun.category.dto.CategoryCreateRequestDto;
import org.example.danggeun.category.dto.CategoryDto;
import org.example.danggeun.category.entity.Category;
import org.example.danggeun.category.repository.CategoryRepository;
import org.example.danggeun.category.service.CategoryService;
import org.example.danggeun.trade.dto.ProductCreateRequestDto;
import org.example.danggeun.trade.dto.ProductDetailResponseDto;
import org.example.danggeun.trade.dto.ProductListResponseDto;
import org.example.danggeun.trade.service.TradeService;
import org.example.danggeun.user.entity.User;
import org.example.danggeun.user.repository.UserRepository;
import org.example.danggeun.user.service.UserService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import jakarta.servlet.http.HttpSession;

import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

@Controller
@RequiredArgsConstructor // final 필드에 대한 생성자 자동 주입
public class TradeController {

    private final TradeService tradeService;
    private final UserRepository userRepository;
    private final UserService userService;
    private final CategoryService categoryService;

    @GetMapping("/trade")
    public String listProducts(Model model) {
        List<ProductListResponseDto> productList = tradeService.findAllProducts();
        model.addAttribute("itemList", productList);
        return "trade/trade"; // 기존의 목록 뷰를 그대로 사용
    }

    @PostMapping("/write")
    public String createProduct(
            @ModelAttribute ProductCreateRequestDto product,
            Authentication authentication  // 이미 인증된 상태이니, 세션 검사 생략
    ) throws IOException {
        Object p = authentication.getPrincipal();
        String loginEmail;
        if (p instanceof OAuth2User oauth2) {
            loginEmail = oauth2.getAttribute("email");
        } else {
            loginEmail = ((UserDetails) p).getUsername();
        }
        User loginUser = userService.findByEmail(loginEmail)
                .orElseThrow(() -> new IllegalStateException("존재하지 않는 사용자: " + loginEmail));

        if (product.getImage() == null || product.getImage().isEmpty()) {
            throw new IllegalArgumentException("이미지 파일이 없습니다.");
        }

        Long productId = tradeService.createProduct(product, loginUser.getId());

        return "redirect:/trade/" + productId;
    }


    @GetMapping("/write")
    public String showWriteForm(
            Authentication authentication,
            Model model
    ) {
        // principal 에서 이메일 추출 (폼/소셜 로그인 공통 처리)
        Object principal = authentication.getPrincipal();
        String loginEmail;
        if (principal instanceof OAuth2User o) {
            loginEmail = o.getAttribute("email");
        } else if (principal instanceof UserDetails u) {
            loginEmail = u.getUsername();
        } else {
            throw new IllegalStateException("알 수 없는 인증 타입");
        }

        model.addAttribute("loginEmail", loginEmail);
        model.addAttribute("product", new ProductCreateRequestDto());

        List<CategoryDto> cats = categoryService.findAll().stream()
                .map(c -> new CategoryDto(c.getId(), c.getName()))
                .collect(Collectors.toList());
        model.addAttribute("categories", cats);

        return "write/write";
    }


    @PostMapping("/trade/submit")
    public String createProduct(@ModelAttribute("product") ProductCreateRequestDto requestDto) throws IOException {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String email = authentication.getName(); // 항상 email로 저장됨

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다."));
        Long userId = user.getId();

        Long productId = tradeService.createProduct(requestDto, userId);
        return "redirect:/tradePost?id=" + productId;
    }

    @GetMapping("/trade/{productId}")
    public String showProductDetail(@PathVariable Long productId, Model model) {
        ProductDetailResponseDto productDto = tradeService.findTradeById(productId);
        model.addAttribute("product", productDto);
        return "trade/tradePost";
    }
}