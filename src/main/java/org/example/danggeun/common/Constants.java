package org.example.danggeun.common;

public final class Constants {

    // 페이징 관련
    public static final int DEFAULT_PAGE_SIZE = 20;
    public static final int MAX_PAGE_SIZE = 100;

    // 파일 업로드 관련
    public static final long MAX_FILE_SIZE = 10 * 1024 * 1024; // 10MB
    public static final String[] ALLOWED_FILE_EXTENSIONS = {"jpg", "jpeg", "png", "gif"};

    // 채팅 관련
    public static final int MAX_MESSAGE_LENGTH = 1000;
    public static final int CHAT_HISTORY_SIZE = 50;

    // 검색 조건
    public static final int MIN_SEARCH_LENGTH = 2;
    public static final int MAX_SEARCH_LENGTH = 50;

    public static final String TRADE_URL_PREFIX = "/trade";

    private Constants() {
        throw new AssertionError("Cannot instantiate constants class");
    }
}
