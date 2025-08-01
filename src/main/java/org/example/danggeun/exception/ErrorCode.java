package org.example.danggeun.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum ErrorCode {
    // 인증 관련
    INVALID_CREDENTIALS("AUTH001", "아이디 또는 비밀번호가 올바르지 않습니다."),
    UNAUTHORIZED("AUTH002", "인증이 필요합니다."),
    ACCESS_DENIED("AUTH003", "접근 권한이 없습니다."),

    // 사용자 관련
    USER_NOT_FOUND("USER001", "사용자를 찾을 수 없습니다."),
    DUPLICATE_EMAIL("USER002", "이미 사용 중인 이메일입니다."),
    DUPLICATE_NICKNAME("USER003", "이미 사용 중인 닉네임입니다."),

    // 상품 관련
    ITEM_NOT_FOUND("ITEM001", "상품을 찾을 수 없습니다."),
    ITEM_ALREADY_SOLD("ITEM002", "이미 판매된 상품입니다."),
    INVALID_ITEM_STATUS("ITEM003", "유효하지 않은 상품 상태입니다."),

    // 채팅 관련
    CHATROOM_NOT_FOUND("CHAT001", "채팅방을 찾을 수 없습니다."),
    CHATROOM_ACCESS_DENIED("CHAT002", "채팅방에 접근할 수 없습니다."),
    MESSAGE_SEND_FAILED("CHAT003", "메시지 전송에 실패했습니다."),

    // 일반
    INVALID_INPUT("COMMON001", "입력값이 올바르지 않습니다."),
    INTERNAL_SERVER_ERROR("COMMON002", "서버 오류가 발생했습니다."),
    FILE_UPLOAD_FAILED("COMMON003", "파일 업로드에 실패했습니다.");

    private final String code;
    private final String message;
}
