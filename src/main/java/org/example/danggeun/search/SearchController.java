package org.example.danggeun.search;

import org.example.danggeun.item.dto.ItemListResponseDto;
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

        Page<ItemListResponseDto> resultPage = createDummyPage(query, pageable);

        model.addAttribute("page", resultPage);
        model.addAttribute("query", query);

        return "search/search";
    }

    private Page<ItemListResponseDto> createDummyPage(String query, Pageable pageable) {
        List<ItemListResponseDto> items = new ArrayList<>();
        int pageNumber = pageable.getPageNumber();
        int pageSize = pageable.getPageSize();


        for (int i = 0; i < pageSize; i++) {
            long itemId = (long) pageNumber * pageSize + i + 1;
            if (itemId > 50) {
                break;
            }

            ItemListResponseDto item = new ItemListResponseDto();
            item.setId(itemId);
            item.setTitle(query + " 관련 상품 (페이지 " + (pageNumber + 1) + " - " + (i + 1) + "번째)");
            item.setPrice(50000 + (int)(itemId * 100));
            item.setLocation("인천 부평구 부평동");
            item.setViewCount(15 + (int)itemId);
            item.setChatCount(41 + (int)itemId);
            item.setImageUrl("/images/item-placeholder.png");
            items.add(item);
        }

        return new PageImpl<>(items, pageable, 50);
    }
}