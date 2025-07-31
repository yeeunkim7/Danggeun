package org.example.danggeun.search.dto;

import lombok.Getter;
import lombok.Setter;
import org.example.danggeun.trade.dto.ItemSearchDto;
import org.example.danggeun.user.dto.UserSimpleDto;
import org.springframework.data.domain.Page;

@Getter
@Setter
public class SearchResultDto {

    private Page<ItemSearchDto> items;
    private Page<UserSimpleDto> users;

    public SearchResultDto() {
    }

    public void setItems(Page<ItemSearchDto> items) {
        this.items = items;
    }

    public void setUsers(Page<UserSimpleDto> users) {
        this.users = users;
    }
}