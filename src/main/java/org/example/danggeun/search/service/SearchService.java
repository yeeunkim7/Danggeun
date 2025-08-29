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
        Page<Trade> trades = tradeRepository.searchByKeyword(keyword, pageable); // 커스텀 쿼리 사용 가정

        return trades.map(t -> ItemSearchDto.builder()
                .id(t.getId())
                .title(highlightKeyword(t.getTitle(), keyword))
                .content(t.getDetail())
                .price(t.getPrice() == null ? 0 : t.getPrice().intValue())
                .imageUrl(t.getImageUrl())
                .location(resolveLocation(t))
                .createdAt(t.getCreatedAt())
                .build());
    }

    private Page<UserSimpleDto> searchUsers(String keyword, Pageable pageable) {
        Page<User> users = userRepository.searchByNickname(keyword, pageable);

        return users.map(u -> UserSimpleDto.builder()
                .id(u.getId())
                .nickname(u.getNickname())
                .profileImageUrl(u.getProfileImageUrl())
                .region(resolveRegion(u))
                .build());
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

    private void saveSearchHistory(String keyword, SearchType type) { }
    private List<String> getRecentSearches(String prefix) { return new ArrayList<>(); }
    private List<String> getPopularSearches(String prefix) { return new ArrayList<>(); }
}
