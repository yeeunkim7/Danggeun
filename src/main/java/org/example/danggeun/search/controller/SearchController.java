package org.example.danggeun.search.controller;

import org.example.danggeun.item.dto.ItemDto; // ItemDto의 실제 경로로 수정하세요.
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.ArrayList;
import java.util.List;

@Controller
public class SearchController {

    @GetMapping("/search")
    public String searchItems(@RequestParam("query") String query, Pageable pageable, Model model) {

        // 아래 코드의 주석을 해제하여 더미 데이터를 사용합니다.
        Page<ItemDto> resultPage = createDummyPage(query, pageable);

        // 이제 resultPage 변수가 존재하므로 오류가 발생하지 않습니다.
        model.addAttribute("page", resultPage);
        model.addAttribute("query", query);

        return "search/search";
    }

    private Page<ItemDto> createDummyPage(String query, Pageable pageable) {
        List<ItemDto> items = new ArrayList<>();
        int pageNumber = pageable.getPageNumber(); // 현재 페이지 번호 (0부터 시작)
        int pageSize = pageable.getPageSize();     // 페이지 당 아이템 수

        // 페이지 번호를 반영하여 아이템 내용 생성
        for (int i = 0; i < pageSize; i++) {
            // 전체 아이템 수(50)를 넘지 않도록 예외 처리
            long itemId = (long) pageNumber * pageSize + i + 1;
            if (itemId > 50) {
                break;
            }

            ItemDto item = new ItemDto();
            item.setId(itemId);
            // 제목에 페이지 번호와 아이템 순번을 모두 표시하여 구별되도록 함
            item.setTitle(query + " 관련 상품 (페이지 " + (pageNumber + 1) + " - " + (i + 1) + "번째)");
            item.setPrice(50000 + (int)(itemId * 100)); // 가격도 조금씩 다르게 설정
            item.setLocation("인천 부평구 부평동");
            item.setViewCount(15 + (int)itemId);
            item.setChatCount(41 + (int)itemId);
            item.setImageUrl("/images/item-placeholder.png");
            items.add(item);
        }

        return new PageImpl<>(items, pageable, 50);
    }
}