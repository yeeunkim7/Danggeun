package org.example.danggeun.common;

public class ErrorMessages {
    // 사용자 관련
    public static final String USER_NOT_FOUND = "사용자를 찾을 수 없습니다.";
    public static final String INVALID_USER_TYPE = "알 수 없는 인증 타입입니다.";

    // 이미지 관련
    public static final String IMAGE_REQUIRED = "이미지 파일이 없습니다.";

    // 입력값 관련
    public static final String INVALID_PRICE = "가격은 0원 이상이어야 합니다.";

    public static final String CATEGORY_NOT_FOUND = "카테고리를 찾을 수 없습니다.";
    public static final String TRADE_NOT_FOUND = "해당 상품을 찾을 수 없습니다. id=%d";

    public static final String SEARCH_KEYWORD_TOO_SHORT = "검색어는 최소 %d자 이상 입력해야 합니다.";

    private ErrorMessages() {
        throw new AssertionError("Cannot instantiate error message class");
    }
}