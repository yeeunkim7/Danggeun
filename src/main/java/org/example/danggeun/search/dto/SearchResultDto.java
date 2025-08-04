package org.example.danggeun.search.dto;

import lombok.Getter;
import lombok.Setter;
import org.example.danggeun.trade.dto.ItemSearchDto;
import org.example.danggeun.user.dto.UserSimpleDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;

import java.util.Collections;

@Getter
@Setter
public class SearchResultDto {

    private Page<ItemSearchDto> items;
    private Page<UserSimpleDto> users;

    public SearchResultDto() {
        this.items = new PageImpl<>(Collections.emptyList());
        this.users = new PageImpl<>(Collections.emptyList());
    }

    public static SearchResultDto empty() {
        SearchResultDto dto = new SearchResultDto();
        dto.setItems(new PageImpl<>(Collections.emptyList()));
        dto.setUsers(new PageImpl<>(Collections.emptyList()));
        return dto;
    }
}
