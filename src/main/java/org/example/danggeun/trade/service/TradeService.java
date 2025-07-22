package org.example.danggeun.trade.service; // 패키지 경로 변경

import lombok.RequiredArgsConstructor;
import org.example.danggeun.address.entity.Address;
import org.example.danggeun.address.repository.AddressRepository;
import org.example.danggeun.category.entity.Category;
import org.example.danggeun.category.repository.CategoryRepository;
import org.example.danggeun.s3.S3Service;
import org.example.danggeun.trade.dto.ProductCreateRequestDto;
import org.example.danggeun.trade.dto.ProductDetailResponseDto;
import org.example.danggeun.trade.dto.ProductListResponseDto;
import org.example.danggeun.trade.entity.Trade;
import org.example.danggeun.trade.repository.TradeRepository;
import org.example.danggeun.user.entity.User;
import org.example.danggeun.user.repository.UserRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor // final 필드 생성자 자동 주입
@Transactional(readOnly = true) // 기본적으로 읽기 전용으로 설정
public class TradeService {

    private final TradeRepository tradeRepository;
    private final UserRepository userRepository;
    private final CategoryRepository categoryRepository;
    private final AddressRepository addressRepository;
    private final S3Service s3Service;


    @Transactional  // 쓰기 작업이므로 readOnly=false
    public Long createProduct(ProductCreateRequestDto dto, Long userId) throws IOException {
        // 1) 판매자, 카테고리 조회
        User seller = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다."));
        Category category = categoryRepository.findById(dto.getCategoryId())
                .orElseThrow(() -> new IllegalArgumentException("카테고리를 찾을 수 없습니다."));

        // 2) S3에 파일 업로드 후 URL 받기
        String imageUrl = null;
        MultipartFile image = dto.getImage();
        if (image != null && !image.isEmpty()) {
            imageUrl = s3Service.uploadFile(image);  // IOException 발생 가능
        }

        // 3) DTO → 엔티티 변환 & 저장
        Trade trade = dto.toEntity(seller, category, imageUrl);
        Trade saved = tradeRepository.save(trade);

        // 4) (선택) 주소 저장
        if (dto.getAddress() != null && !dto.getAddress().isBlank()) {
            Address addr = Address.builder()
                    .detail(dto.getAddress())
                    .user(seller)
                    .build();
            addressRepository.save(addr);
        }

        return saved.getId();
    }

    public List<ProductListResponseDto> findAllProducts() {
        List<Trade> products = tradeRepository.findAllWithSeller();

        return products.stream()
                .map(ProductListResponseDto::new) // DTO 변환
                .collect(Collectors.toList());
    }

    public ProductDetailResponseDto findTradeById(Long productId) {
        // 2. 클래스 이름(TradeRepository)이 아닌, 주입된 객체(tradeRepository)를 사용하도록 수정
        Trade trade = tradeRepository.findById(productId)
                .orElseThrow(() -> new IllegalArgumentException("해당 상품을 찾을 수 없습니다. id=" + productId));

        // 3. ProductDetailResponseDto 생성자가 (Trade entity)만 받도록 수정되었다고 가정
        return new ProductDetailResponseDto(trade);
    }

    public Page<ProductListResponseDto> findAllProducts(Pageable pageable) {
        return tradeRepository.findAll(pageable)
                .map(ProductListResponseDto::new);
    }

    public Page<ProductListResponseDto> searchProducts(String keyword, Pageable pageable) {
        return tradeRepository.findByTitleContainingIgnoreCase(keyword, pageable)
                .map(ProductListResponseDto::new);
    }

    public Optional<Trade> findById(Long id) {
        return tradeRepository.findById(id);
    }
}