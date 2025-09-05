package org.example.danggeun.search.service;

import jakarta.validation.ValidationException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.example.danggeun.search.dto.SearchResultDto;
import org.example.danggeun.search.type.SearchType;
import org.example.danggeun.trade.dto.ItemSearchDto;
import org.example.danggeun.trade.entity.Trade;
import org.example.danggeun.trade.repository.TradeRepository;
import org.example.danggeun.user.dto.UserSimpleDto;
import org.example.danggeun.user.entity.User;
import org.example.danggeun.user.repository.UserRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional(readOnly = true)
public class SearchService {

    private final TradeRepository tradeRepository;
    private final UserRepository userRepository;

    public SearchResultDto search(String keyword, SearchType type, Pageable pageable) {
        validateKeyword(keyword);
        String normalizedKeyword = normalizeKeyword(keyword);

        SearchResultDto result = new SearchResultDto();

        switch (type) {
            case ALL -> {
                result.setItems(searchItems(normalizedKeyword, pageable));
                result.setUsers(searchUsers(normalizedKeyword, PageRequest.of(0, 5)));
            }
            case ITEM -> result.setItems(searchItems(normalizedKeyword, pageable));
            case USER -> result.setUsers(searchUsers(normalizedKeyword, pageable));
        }

        saveSearchHistory(keyword, type);
        return result;
    }

    private Page<ItemSearchDto> searchItems(String keyword, Pageable pageable) {
        Page<Trade> trades = tradeRepository.searchByKeyword(keyword, pageable);

        return trades.map(t -> ItemSearchDto.builder()
                .id(t.getId())
                .title(highlightKeyword(t.getTitle(), keyword))
                .content(t.getDetail())
                .price(t.getPrice() == null ? 0 : t.getPrice().intValue())
                .imageUrl(resolveImageUrl(t.getImageUrl(), t.getTitle()))
                .location(resolveLocation(t))
                .createdAt(t.getCreatedAt())
                .build());
    }

    private Page<UserSimpleDto> searchUsers(String keyword, Pageable pageable) {
        Page<User> users = userRepository.searchByNickname(keyword, pageable);

        return users.map(u -> UserSimpleDto.builder()
                .id(u.getId())
                .nickname(u.getNickname())
                .profileImageUrl(resolveProfileImageUrl(u.getProfileImageUrl()))
                .region(resolveRegion(u))
                .build());
    }

    private String resolveImageUrl(String imageUrl, String productTitle) {
        // null이거나 빈 문자열인 경우 상품 제목에 따라 적절한 기본 이미지 반환
        if (imageUrl == null || imageUrl.trim().isEmpty()) {
            if (productTitle != null) {
                String title = productTitle.toLowerCase();
                if (title.contains("노트북") || title.contains("맥북") || title.contains("laptop") || title.contains("갤럭시북")) {
                    return "mac.jpeg";
                } else if (title.contains("자전거") || title.contains("bike")) {
                    return "bike.jpeg";
                } else if (title.contains("책") || title.contains("book")) {
                    return "book.jpeg";
                } else if (title.contains("책상") || title.contains("desk")) {
                    return "desk.jpeg";
                } else if (title.contains("그램") || title.contains("gram")) {
                    return "gram.jpeg";
                }
            }
            return "bike.jpeg"; // 최종 기본값
        }

        // 이미 전체 경로인 경우 그대로 반환
        if (imageUrl.startsWith("/img/") || imageUrl.startsWith("http")) {
            return imageUrl;
        }

        // 파일명만 있는 경우 그대로 반환
        return imageUrl;
    }

    private String resolveProfileImageUrl(String profileImageUrl) {
        if (profileImageUrl == null || profileImageUrl.trim().isEmpty()) {
            return "profile-placeholder.jpeg"; // 기본 프로필 이미지
        }

        if (profileImageUrl.startsWith("/img/") || profileImageUrl.startsWith("http")) {
            return profileImageUrl;
        }

        return profileImageUrl;
    }

    public List<String> getAutoComplete(String prefix) {
        if (prefix == null || prefix.trim().length() < 2) return Collections.emptyList();

        String p = prefix.trim();
        List<String> suggestions = new ArrayList<>();
        suggestions.addAll(getRecentSearches(p));
        suggestions.addAll(getPopularSearches(p));
        suggestions.addAll(tradeRepository.findTitlesByPrefix(p.toLowerCase(), PageRequest.of(0, 5)));

        return suggestions.stream().distinct().limit(10).collect(Collectors.toList());
    }

    private void validateKeyword(String keyword) {
        if (StringUtils.isBlank(keyword)) throw new ValidationException("검색어를 입력해주세요.");
        if (keyword.trim().length() < 2) throw new ValidationException("검색어는 2자 이상 입력해주세요.");
        if (keyword.length() > 50) throw new ValidationException("검색어는 50자 이내로 입력해주세요.");
    }

    private String normalizeKeyword(String keyword) {
        String k = keyword.trim().replaceAll("\\s+", " ");
        k = k.replaceAll("[^a-zA-Z0-9가-힣\\s]", "");
        return k;
    }

    private String highlightKeyword(String text, String keyword) {
        if (text == null || keyword == null) return text;
        String regex = "(?i)(" + Pattern.quote(keyword) + ")";
        return text.replaceAll(regex, "<mark>$1</mark>");
    }

    private String resolveLocation(Trade t) {
        try {
            if (t.getSeller() != null && t.getSeller().getAddress() != null && !t.getSeller().getAddress().isEmpty()) {
                return String.valueOf(t.getSeller().getAddress().get(0).getDetail());
            }
        } catch (Exception ignored) { }
        return "지역 정보 없음";
    }

    private String resolveRegion(User u) {
        try {
            if (u.getRegion() != null) return u.getRegion();
            if (u.getAddress() != null && !u.getAddress().isEmpty()) {
                return String.valueOf(u.getAddress().get(0).getDetail());
            }
        } catch (Exception ignored) { }
        return "지역 정보 없음";
    }

    private void saveSearchHistory(String keyword, SearchType type) {
        // 검색 기록 저장 로직 (향후 구현)
    }

    private List<String> getRecentSearches(String prefix) {
        return new ArrayList<>();
    }

    private List<String> getPopularSearches(String prefix) {
        return new ArrayList<>();
    }
}