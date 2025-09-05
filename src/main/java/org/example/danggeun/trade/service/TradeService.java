package org.example.danggeun.trade.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
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
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Slf4j
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
        log.info("=== TradeService.createProduct 시작 ===");
        log.info("사용자 ID: {}, 카테고리 ID: {}", userId, dto.getCategoryId());

        User seller = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다: " + userId));
        log.info("판매자 조회 성공: {}", seller.getEmail());

        // categoryId가 null인 경우 기본 카테고리 사용
        Category category;
        if (dto.getCategoryId() != null) {
            category = categoryRepository.findById(dto.getCategoryId())
                    .orElseThrow(() -> new IllegalArgumentException(ErrorMessages.CATEGORY_NOT_FOUND));
        } else {
            // 기본 카테고리 설정 (예: ID 1번)
            category = categoryRepository.findById(1L)
                    .orElseThrow(() -> new IllegalArgumentException("기본 카테고리를 찾을 수 없습니다."));
        }

        String imageUrl = null;
        MultipartFile image = dto.getNormalizedImage();

        if (image != null && !image.isEmpty()) {
            try {
                imageUrl = s3Service.uploadFile(image);
                log.info("이미지 업로드 성공: {}", imageUrl);
            } catch (IOException e) {
                log.error("S3 업로드 실패, 기본 이미지 사용", e);
                imageUrl = getDefaultImageByTitle(dto.getNormalizedTitle());
            }
        } else {
            // 이미지가 없는 경우 제목에 따라 적절한 기본 이미지 설정
            imageUrl = getDefaultImageByTitle(dto.getNormalizedTitle());
            log.info("기본 이미지 사용: {}", imageUrl);
        }

        Trade trade = dto.toEntity(seller, category, imageUrl);
        Trade saved = tradeRepository.save(trade);
        log.info("상품 저장 완료 - ID: {}, 제목: {}", saved.getId(), saved.getTitle());

        // 주소 정보 저장
        if (dto.getAddress() != null && !dto.getAddress().isBlank()) {
            Address addr = Address.builder()
                    .detail(dto.getAddress())
                    .user(seller)
                    .build();
            addressRepository.save(addr);
            log.info("주소 정보 저장 완료: {}", dto.getAddress());
        }

        return saved.getId();
    }

    @Transactional
    public void updateProduct(Long productId, TradeCreateRequestDto dto, Long userId) throws IOException {
        Trade trade = tradeRepository.findById(productId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 상품입니다."));

        // 권한 체크
        if (!trade.getSeller().getId().equals(userId)) {
            throw new IllegalArgumentException("수정 권한이 없습니다.");
        }

        // 이미지 처리
        String imageUrl = trade.getImageUrl();
        MultipartFile image = dto.getNormalizedImage();

        if (image != null && !image.isEmpty()) {
            try {
                imageUrl = s3Service.uploadFile(image);
                log.info("이미지 업데이트 성공: {}", imageUrl);
            } catch (IOException e) {
                log.error("S3 업로드 실패, 기존 이미지 유지", e);
            }
        }

        // Trade 엔티티 업데이트 (5개 파라미터로 수정)
        trade.updateInfo(
                dto.getNormalizedTitle(),
                dto.getNormalizedDetail(),
                dto.getNormalizedPrice(),
                imageUrl,
                dto.getAddress()  // address 파라미터 추가
        );

        log.info("상품 업데이트 완료 - ID: {}, 제목: {}", productId, dto.getNormalizedTitle());
    }

    public List<TradeListResponseDto> findAllProducts() {
        log.info("findAllProducts() 호출됨");

        List<Trade> products = tradeRepository.findAll().stream()
                .sorted((a, b) -> b.getCreatedAt().compareTo(a.getCreatedAt()))
                .collect(Collectors.toList());

        log.info("데이터베이스에서 조회된 상품 수: {}", products.size());

        if (products.isEmpty()) {
            log.warn("데이터베이스에 상품이 없습니다.");
            return new ArrayList<>();
        }

        List<TradeListResponseDto> result = products.stream()
                .map(trade -> {
                    TradeListResponseDto dto = new TradeListResponseDto(trade);

                    // 임시 하드코딩 매핑 (문제 해결까지)
                    String imageUrl = trade.getImageUrl();
                    if (trade.getId().equals(191L)) { // 갤럭시북 노트북
                        imageUrl = "/img/mac.jpeg";
                    } else if (trade.getId().equals(192L)) { // 이케아 책상
                        imageUrl = "/img/desk.jpeg";
                    } else if (trade.getId().equals(193L)) { // 참고서
                        imageUrl = "/img/book.jpeg";
                    } else {
                        imageUrl = resolveImageUrl(trade.getImageUrl(), trade.getTitle());
                    }

                    dto.setImageUrl(imageUrl);

                    log.info("DTO 생성 - ID: {}, 제목: {}, 이미지URL: {}",
                            dto.getId(), dto.getTitle(), imageUrl);

                    return dto;
                })
                .collect(Collectors.toList());

        log.info("최종 반환될 DTO 수: {}", result.size());
        return result;
    }

    public TradeDetailResponseDto findTradeById(Long productId) {
        Trade trade = tradeRepository.findById(productId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 상품: " + productId));

        // 조회수 증가
        trade.increaseViewCount();
        tradeRepository.save(trade);

        TradeDetailResponseDto dto = new TradeDetailResponseDto(trade);
        // 이미지 URL 안전 처리
        dto.setImageUrl(resolveImageUrl(trade.getImageUrl(), trade.getTitle()));
        return dto;
    }

    public Page<TradeListResponseDto> findAllProducts(Pageable pageable) {
        return tradeRepository.findAll(pageable)
                .map(trade -> {
                    TradeListResponseDto dto = new TradeListResponseDto(trade);

                    // 페이지네이션된 결과도 임시 하드코딩 매핑
                    String imageUrl = trade.getImageUrl();
                    if (trade.getId().equals(191L)) { // 갤럭시북 노트북
                        imageUrl = "/img/mac.jpeg";
                    } else if (trade.getId().equals(192L)) { // 이케아 책상
                        imageUrl = "/img/desk.jpeg";
                    } else if (trade.getId().equals(193L)) { // 참고서
                        imageUrl = "/img/book.jpeg";
                    } else {
                        imageUrl = resolveImageUrl(trade.getImageUrl(), trade.getTitle());
                    }

                    dto.setImageUrl(imageUrl);
                    return dto;
                });
    }

    public Page<TradeListResponseDto> searchProducts(String keyword, Pageable pageable) {
        if (keyword == null || keyword.trim().length() < Constants.MIN_SEARCH_LENGTH) {
            throw new IllegalArgumentException(
                    String.format(ErrorMessages.SEARCH_KEYWORD_TOO_SHORT, Constants.MIN_SEARCH_LENGTH)
            );
        }
        return tradeRepository.searchByKeyword(keyword, pageable)
                .map(trade -> {
                    TradeListResponseDto dto = new TradeListResponseDto(trade);

                    // 검색된 상품도 임시 하드코딩 매핑
                    String imageUrl = trade.getImageUrl();
                    if (trade.getId().equals(191L)) { // 갤럭시북 노트북
                        imageUrl = "/img/mac.jpeg";
                    } else if (trade.getId().equals(192L)) { // 이케아 책상
                        imageUrl = "/img/desk.jpeg";
                    } else if (trade.getId().equals(193L)) { // 참고서
                        imageUrl = "/img/book.jpeg";
                    } else {
                        imageUrl = resolveImageUrl(trade.getImageUrl(), trade.getTitle());
                    }

                    dto.setImageUrl(imageUrl);
                    return dto;
                });
    }

    public Optional<Trade> findById(Long id) {
        return tradeRepository.findById(id);
    }

    /**
     * 이미지 URL을 안전하게 처리하는 메서드
     */
    private String resolveImageUrl(String imageUrl, String productTitle) {
        // S3 URL이거나 완전한 URL인 경우 그대로 반환
        if (imageUrl != null && !imageUrl.trim().isEmpty()) {
            if (imageUrl.startsWith("http") || imageUrl.startsWith("/")) {
                return imageUrl;
            }
            // 파일명만 있는 경우 /img/ 경로 추가
            if (!imageUrl.startsWith("/img/")) {
                return "/img/" + imageUrl;
            }
            return imageUrl;
        }

        // null이거나 빈 문자열인 경우 상품 제목에 따라 기본 이미지 반환
        return getDefaultImageByTitle(productTitle);
    }

    /**
     * 상품 제목에 따른 적절한 기본 이미지 반환
     */
    private String getDefaultImageByTitle(String productTitle) {
        if (productTitle != null) {
            String title = productTitle.toLowerCase();
            if (title.contains("노트북") || title.contains("맥북") || title.contains("laptop") ||
                    title.contains("갤럭시북") || title.contains("컴퓨터")) {
                return "/img/mac.jpeg";
            } else if (title.contains("자전거") || title.contains("bike")) {
                return "/img/bike.jpeg";
            } else if (title.contains("책") || title.contains("book")) {
                return "/img/book.jpeg";
            } else if (title.contains("책상") || title.contains("desk")) {
                return "/img/desk.jpeg";
            } else if (title.contains("그램") || title.contains("gram")) {
                return "/img/gram.jpeg";
            }
        }
        return "/img/mac.jpeg"; // 최종 기본값
    }
}