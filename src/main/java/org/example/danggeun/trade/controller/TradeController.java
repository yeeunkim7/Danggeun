package org.example.danggeun.trade.controller; // 패키지 경로를 product로 변경하는 것을 권장합니다.

import lombok.RequiredArgsConstructor;
import org.example.danggeun.category.entity.Category;
import org.example.danggeun.category.repository.CategoryRepository;
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
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import jakarta.servlet.http.HttpSession;

import java.io.IOException;
import java.util.List;

@Controller
@RequiredArgsConstructor // final 필드에 대한 생성자 자동 주입
public class TradeController {

    private final TradeService tradeService;
    private final UserRepository userRepository;
    private final CategoryRepository categoryRepository;

    @GetMapping("/trade")
    public String listProducts(Model model) {
        List<ProductListResponseDto> productList = tradeService.findAllProducts();
        model.addAttribute("itemList", productList);
        return "trade/trade"; // 기존의 목록 뷰를 그대로 사용
    }

    @PostMapping("/write")
    public ResponseEntity<String> createProduct(@ModelAttribute ProductCreateRequestDto requestDto, HttpSession session) {
        try {
            Long userId = (Long) session.getAttribute("userId");
            if (userId == null) {
                // 인증되지 않은 사용자는 401 Unauthorized 오류 반환
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("로그인이 필요합니다.");
            }

            // DTO의 image 필드에 파일이 잘 담겼는지 확인 (디버깅용)
            if (requestDto.getImage() == null || requestDto.getImage().isEmpty()) {
                return ResponseEntity.badRequest().body("이미지 파일이 없습니다.");
            }

            tradeService.createProduct(requestDto, userId);

            // 성공 시 200 OK 응답
            return ResponseEntity.ok("작성 완료");

        } catch (IOException e) {
            e.printStackTrace();
            // 서버 내부 오류 발생 시 500 Internal Server Error 응답
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("파일 처리 중 오류 발생");
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("서버 오류 발생");
        }

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