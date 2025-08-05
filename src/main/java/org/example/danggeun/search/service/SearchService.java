package org.example.danggeun.search.service;

import jakarta.validation.ValidationException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.example.danggeun.search.dto.SearchResultDto;
import org.example.danggeun.search.type.SearchType;
import org.example.danggeun.trade.dto.ItemSearchDto;
import org.example.danggeun.trade.entity.Item;
import org.example.danggeun.trade.repository.ItemRepository;
import org.example.danggeun.user.dto.UserSimpleDto;
import org.example.danggeun.user.entity.User;
import org.example.danggeun.user.repository.UserRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.PageRequest;
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

    private final ItemRepository itemRepository;
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
        Page<Item> items = itemRepository.searchByKeyword(keyword, pageable);
        return items.map(item -> ItemSearchDto.builder()
                .id(item.getId())
                .title(highlightKeyword(item.getTitle(), keyword))
                .content(item.getContent())
                .price(item.getPrice())
                .imageUrl(item.getFirstImageUrl())
                .location(item.getLocation())
                .createdAt(item.getCreatedAt())
                .build());
    }

    private Page<UserSimpleDto> searchUsers(String keyword, Pageable pageable) {
        Page<User> users = userRepository.searchByNickname(keyword, pageable);
        return users.map(user -> UserSimpleDto.builder()
                .id(user.getId())
                .nickname(user.getNickname())
                .profileImageUrl(user.getProfileImageUrl())
                .region(user.getRegion())
                .build());
    }

    public List<String> getAutoComplete(String prefix) {
        if (prefix.length() < 2) return Collections.emptyList();
        List<String> suggestions = new ArrayList<>();
        suggestions.addAll(getRecentSearches(prefix));
        suggestions.addAll(getPopularSearches(prefix));
        suggestions.addAll(itemRepository.findTitlesByPrefix(prefix.toLowerCase(), PageRequest.of(0, 5)));
        return suggestions.stream().distinct().limit(10).collect(Collectors.toList());
    }

    private void validateKeyword(String keyword) {
        if (StringUtils.isBlank(keyword)) throw new ValidationException("검색어를 입력해주세요.");
        if (keyword.length() < 2) throw new ValidationException("검색어는 2자 이상 입력해주세요.");
        if (keyword.length() > 50) throw new ValidationException("검색어는 50자 이내로 입력해주세요.");
    }

    private String normalizeKeyword(String keyword) {
        keyword = keyword.trim().replaceAll("\\s+", " ");
        keyword = keyword.replaceAll("[^a-zA-Z0-9가-힣\\s]", "");
        return keyword;
    }

    private String highlightKeyword(String text, String keyword) {
        if (text == null || keyword == null) return text;
        String regex = "(?i)(" + Pattern.quote(keyword) + ")";
        return text.replaceAll(regex, "<mark>$1</mark>");
    }

    private void saveSearchHistory(String keyword, SearchType type) {
        // TODO: 검색 기록 저장 구현 예정
    }

    private List<String> getRecentSearches(String prefix) {
        return new ArrayList<>();
    }

    private List<String> getPopularSearches(String prefix) {
        return new ArrayList<>();
    }
}
