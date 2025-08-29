package org.example.danggeun.trade.service; // 패키지 경로 변경

import lombok.RequiredArgsConstructor;
import org.example.danggeun.address.entity.Address;
import org.example.danggeun.address.repository.AddressRepository;
import org.example.danggeun.category.entity.Category;
import org.example.danggeun.category.repository.CategoryRepository;
import org.example.danggeun.common.Constants;
import org.example.danggeun.common.ErrorMessages;
import org.example.danggeun.s3.service.S3Service;
import org.example.danggeun.trade.dto.TradeCreateRequestDto;
import org.example.danggeun.trade.dto.TradeDetailResponseDto;
import org.example.danggeun.trade.dto.TradeListResponseDto;
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
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class TradeService {

    private final TradeRepository tradeRepository;
    private final UserRepository userRepository;
    private final CategoryRepository categoryRepository;
    private final AddressRepository addressRepository;
    private final S3Service s3Service;


    @Transactional
    public Long createProduct(TradeCreateRequestDto dto, Long userId) throws IOException {
        User seller = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException(ErrorMessages.USER_NOT_FOUND));
        Category category = categoryRepository.findById(dto.getCategoryId())
                .orElseThrow(() -> new IllegalArgumentException(ErrorMessages.CATEGORY_NOT_FOUND));


        String imageUrl = null;
        MultipartFile image = dto.getImage();
        if (image != null && !image.isEmpty()) {
            imageUrl = s3Service.uploadFile(image);
        }

        Trade trade = dto.toEntity(seller, category, imageUrl);
        Trade saved = tradeRepository.save(trade);

        if (dto.getAddress() != null && !dto.getAddress().isBlank()) {
            Address addr = Address.builder()
                    .detail(dto.getAddress())
                    .user(seller)
                    .build();
            addressRepository.save(addr);
        }

        return saved.getId();
    }

    public List<TradeListResponseDto> findAllProducts() {
        List<Trade> products = tradeRepository.findAllWithSeller();

        return products.stream()
                .map(TradeListResponseDto::new)
                .collect(Collectors.toList());
    }

    public TradeDetailResponseDto findTradeById(Long productId) {
        Trade trade = tradeRepository.findById(productId)
                .orElseThrow(() -> new IllegalArgumentException(String.format(ErrorMessages.TRADE_NOT_FOUND, productId) + productId));

        return new TradeDetailResponseDto(trade);
    }

    public Page<TradeListResponseDto> findAllProducts(Pageable pageable) {
        return tradeRepository.findAll(pageable)
                .map(TradeListResponseDto::new);
    }

    public Page<TradeListResponseDto> searchProducts(String keyword, Pageable pageable) {
        if (keyword == null || keyword.trim().length() < Constants.MIN_SEARCH_LENGTH) {
            throw new IllegalArgumentException(
                    String.format(ErrorMessages.SEARCH_KEYWORD_TOO_SHORT, Constants.MIN_SEARCH_LENGTH)
            );
        }
        return tradeRepository.searchByKeyword(keyword, pageable)
                .map(TradeListResponseDto::new);
    }

    public Optional<Trade> findById(Long id) {
        return tradeRepository.findById(id);
    }
}