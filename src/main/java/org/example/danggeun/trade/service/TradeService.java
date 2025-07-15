package org.example.danggeun.trade.service; // 패키지 경로 변경

import lombok.RequiredArgsConstructor;
import org.example.danggeun.category.entity.Category;
import org.example.danggeun.category.repository.CategoryRepository;
import org.example.danggeun.common.FileStore;
import org.example.danggeun.trade.dto.ProductCreateRequestDto;
import org.example.danggeun.trade.dto.ProductListResponseDto;
import org.example.danggeun.trade.entity.Trade;
import org.example.danggeun.trade.repository.TradeRepository;
import org.example.danggeun.user.entity.User;
import org.example.danggeun.user.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor // final 필드 생성자 자동 주입
@Transactional(readOnly = true) // 기본적으로 읽기 전용으로 설정
public class TradeService {

    private final TradeRepository productRepository;
    private final UserRepository userRepository;
    private final CategoryRepository categoryRepository;
    private final FileStore fileStore; // 파일 저장 컴포넌트 주입

    @Transactional // 쓰기 작업이므로 readOnly=false 적용
    public Long createProduct(ProductCreateRequestDto requestDto, Long userId) throws IOException {
        // 1. 엔티티 조회 (판매자, 카테고리)
        User seller = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다."));
        Category category = categoryRepository.findById(requestDto.getCategoryId())
                .orElseThrow(() -> new IllegalArgumentException("카테고리를 찾을 수 없습니다."));

        // 2. 파일 저장 및 URL 획득
        String imageUrl = fileStore.storeFile(requestDto.getImage());

        // 3. DTO를 엔티티로 변환하여 DB에 저장
        Trade trade = requestDto.toEntity(seller, category, imageUrl);
        Trade savetrade = productRepository.save(trade);

        return savetrade.getId();
    }

    public List<ProductListResponseDto> findAllProducts() {
        // Repository에서 Fetch Join을 사용한 메소드 호출
        List<Trade> products = productRepository.findAllWithSeller();

        return products.stream()
                .map(ProductListResponseDto::new) // DTO 변환
                .collect(Collectors.toList());
    }

}