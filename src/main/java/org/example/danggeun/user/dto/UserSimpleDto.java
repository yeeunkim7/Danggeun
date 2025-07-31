package org.example.danggeun.user.dto;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class UserSimpleDto {
    private Long id;
    private String nickname;
    private String profileImageUrl;
    private String region;
}