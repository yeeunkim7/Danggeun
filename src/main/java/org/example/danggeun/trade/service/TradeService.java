package org.example.danggeun.trade.service;

import lombok.RequiredArgsConstructor;
import org.example.danggeun.address.entity.Address;
import org.example.danggeun.address.repository.AddressRepository;
import org.example.danggeun.category.entity.Category;
import org.example.danggeun.category.repository.CategoryRepository;
import org.example.danggeun.common.FileStore;
import org.example.danggeun.trade.dto.TradeCreateRequestDto;
import org.example.danggeun.trade.dto.TradeDetailResponseDto;
import org.example.danggeun.trade.dto.TradeListResponseDto;
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
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class TradeService {

    private final TradeRepository tradeRepository;
    private final UserRepository userRepository;
    private final CategoryRepository categoryRepository;
    private final FileStore fileStore;
    private final AddressRepository addressRepository;

    @Transactional
    public Long createProduct(TradeCreateRequestDto requestDto, Long userId) throws IOException {

        User seller = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다."));
        Category category = categoryRepository.findById(requestDto.getCategoryId())
                .orElseThrow(() -> new IllegalArgumentException("카테고리를 찾을 수 없습니다."));

        String imageUrl = fileStore.storeFile(requestDto.getImage());

        Trade trade = requestDto.toEntity(seller, category, imageUrl);

        if (requestDto.getAddress() != null && !requestDto.getAddress().isBlank()) {
            Address newAddress = Address.builder()
                    .detail(requestDto.getAddress())
                    .user(seller)
                    .build();
            addressRepository.save(newAddress);
        }

        Trade savetrade = tradeRepository.save(trade);

        return savetrade.getId();
    }

    public List<TradeListResponseDto> findAllProducts() {
        List<Trade> products = tradeRepository.findAllWithSeller();

        return products.stream()
                .map(TradeListResponseDto::new)
                .collect(Collectors.toList());
    }

    public TradeDetailResponseDto findTradeById(Long productId) {
        Trade trade = tradeRepository.findById(productId)
                .orElseThrow(() -> new IllegalArgumentException("해당 상품을 찾을 수 없습니다. id=" + productId));

        return new TradeDetailResponseDto(trade);
    }


}