package org.example.danggeun.trade.controller; // 패키지 경로를 product로 변경하는 것을 권장합니다.

import lombok.RequiredArgsConstructor;
import org.example.danggeun.trade.dto.ProductCreateRequestDto;
import org.example.danggeun.trade.dto.ProductDetailResponseDto;
import org.example.danggeun.trade.dto.ProductListResponseDto;
import org.example.danggeun.trade.service.TradeService;
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

    /**
     * 상품 목록 페이지를 보여줍니다.
     * @param model 뷰에 데이터를 전달하기 위한 모델
     * @return 상품 목록 뷰의 경로
     */
    @GetMapping("/trade")
    public String listProducts(Model model) {
        List<ProductListResponseDto> productList = tradeService.findAllProducts();
        model.addAttribute("itemList", productList);
        return "trade/trade"; // 기존의 목록 뷰를 그대로 사용
    }


    @GetMapping("/trade/new")
    public String showCreateForm() {
        return "trade/create-form"; // 상품 등록을 위한 새 폼 페이지 (trade/create-form.html)
    }

    @PostMapping("/trade")
    public String createProduct(@ModelAttribute ProductCreateRequestDto requestDto, HttpSession session) throws IOException {
        Long userId = (Long) session.getAttribute("userId");

        if (userId == null) {
            return "redirect:/login";
        }

        Long newProductId = tradeService.createProduct(requestDto, userId);

        return "redirect:/trade/" + newProductId;
    }

    @GetMapping("/trade/{productId}")
    public String showProductDetail(@PathVariable Long productId, Model model) {
        ProductDetailResponseDto productDto = tradeService.findTradeById(productId);
        model.addAttribute("product", productDto);
        return "trade/detail"; // 상품 상세 정보 뷰 (trade/detail.html)
    }
}