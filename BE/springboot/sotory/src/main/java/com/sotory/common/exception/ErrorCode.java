package com.sotory.common.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public enum ErrorCode {
    // 시스템 에러
    SYSTEM_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "시스템 에러 발생"),
    INVALID_KAKAO_TOKEN(HttpStatus.UNAUTHORIZED, "카카오 액세스 토큰이 유효하지 않습니다."),
    SERVER_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "서버 내부 오류가 발생했습니다. 잠시 후 다시 시도해주세요."),
    // 토큰 관련 에러
    INVALID_TOKEN(HttpStatus.UNAUTHORIZED, "토큰 유효성 검증에 실패하였습니다."),
    TOKEN_EXPIRED(HttpStatus.UNAUTHORIZED, "토큰 유효기간이 만료되었습니다."),
    INVALID_TOKEN_REQUEST(HttpStatus.BAD_REQUEST, "토큰 요청 형식이 잘못되었습니다."),
    TOKEN_ERROR(HttpStatus.UNAUTHORIZED, "토큰 인증에 실패하였습니다."),

    INVALID_REFRESH_TOKEN_REDIS(HttpStatus.UNAUTHORIZED, "유효하지 않은 리프레시 토큰입니다 (Redis)."),
    AUTH_USER_NOT_FOUND(HttpStatus.NOT_FOUND, "AuthUser 정보가 존재하지 않습니다."),
    AUTH_TOKEN_NOT_FOUND(HttpStatus.NOT_FOUND, "AuthToken 정보가 존재하지 않습니다."),
    INVALID_REFRESH_TOKEN_DB(HttpStatus.UNAUTHORIZED, "DB 내 리프레시 토큰이 유효하지 않습니다."),
    REFRESH_TOKEN_VERSION_MISMATCH(HttpStatus.UNAUTHORIZED, "리프레시 토큰 버전이 일치하지 않습니다."),

    TOKEN_REISSUE_CONFLICT(HttpStatus.CONFLICT, "동시 토큰 재발급 충돌이 발생했습니다. 잠시 후 다시 시도해주세요."),


    // 로그아웃
    INVALID_REQUEST(HttpStatus.BAD_REQUEST, "필수 데이터가 유효하지 않습니다."),
    UNAUTHORIZED(HttpStatus.UNAUTHORIZED, "유효하지 않은 액세스 토큰입니다."),
    FORBIDDEN(HttpStatus.FORBIDDEN, "해당 리소스에 대한 접근 권한이 없습니다."),

    // 데이터 관련 에러
    DATA_NOT_FOUND(HttpStatus.NOT_FOUND, "데이터 조회에 실패했습니다."),
    DATA_SAVE_FAILED(HttpStatus.INTERNAL_SERVER_ERROR, "데이터 저장, 업데이트에 실패했습니다."),
    DATA_DELETE_FAILED(HttpStatus.INTERNAL_SERVER_ERROR, "데이터 삭제에 실패했습니다."),
    DATA_FORBIDDEN_ACCESS(HttpStatus.FORBIDDEN, "데이터에 대한 접근 권한이 없습니다."),
    INVALID_PARAMETER(HttpStatus.BAD_REQUEST, "파라미터 형식이 잘못되었습니다."),
    DATA_FORBIDDEN_UPDATE(HttpStatus.FORBIDDEN, "데이터에 대한 수정/삭제 권한이 없습니다."),
    VALIDATION_ERROR(HttpStatus.BAD_REQUEST, "유효성 검사에 실패했습니다."),


    // 사용자 관련 에러
    DUPLICATE_EMAIL(HttpStatus.CONFLICT, "이미 존재하는 이메일입니다."),
    DUPLICATE_NICKNAME(HttpStatus.CONFLICT, "이미 존재하는 닉네임입니다."),
    SEND_EMAIL_CODE_FAILED(HttpStatus.INTERNAL_SERVER_ERROR, "인증 코드 전송 실패"),
    EMAIL_CERTIFICATION_FAILED(HttpStatus.BAD_REQUEST, "이메일 인증 실패"),
    DUPLICATE_USER(HttpStatus.CONFLICT, "이미 가입된 회원입니다."),
    JOIN_SOCIAL_FAILED(HttpStatus.INTERNAL_SERVER_ERROR, "간편 회원가입 실패"),
    USER_NOT_FOUND(HttpStatus.NOT_FOUND, "존재하지 않는 회원입니다."),
    INVALID_EMAIL_FORMAT(HttpStatus.BAD_REQUEST, "이메일 형식이 잘못되었습니다."),
    INVALID_PASSWORD_FORMAT(HttpStatus.BAD_REQUEST, "비밀번호 형식이 잘못되었습니다."),
    INVALID_USER_INFO(HttpStatus.BAD_REQUEST, "잘못된 사용자 요청입니다. 입력한 정보를 다시 확인해주세요."),
    WITHDRAW_USER(HttpStatus.GONE, "탈퇴한 사용자입니다."),
    INVALID_LOGIN_EMAIL(HttpStatus.BAD_REQUEST, "이메일이 잘못되었습니다."),

    DIARY_NOT_FOUND(HttpStatus.NOT_FOUND, "오늘 날짜의 일기가 존재하지 않습니다."),
    DIARY_CONTAINS_BAD_WORDS(HttpStatus.BAD_REQUEST, "비속어가 포함된 일기 내용입니다."),

    // 인증 관련 에러
    LOGIN_FAILED(HttpStatus.UNAUTHORIZED, "로그인에 실패하였습니다."),
    LOGIN_SOCIAL_FAILED(HttpStatus.UNAUTHORIZED, "간편 로그인 실패"),
    INCORRECT_PASSWORD(HttpStatus.UNAUTHORIZED, "현재 비밀번호가 올바르지 않습니다."),
    SAME_PASSWORD(HttpStatus.BAD_REQUEST, "현재 비밀번호와 동일한 비밀번호입니다."),

    // 파일 관련 에러
    FILE_UPLOAD_FAILED(HttpStatus.INTERNAL_SERVER_ERROR, "파일 업로드 실패"),
    IMAGE_ANALYZE_FAILED(HttpStatus.INTERNAL_SERVER_ERROR, "이미지 분석 실패"),
    FILE_DELETE_IS_FAILED(HttpStatus.INTERNAL_SERVER_ERROR, "파일 삭제에 실패하였습니다."),
    FILE_DOES_NOT_EXIST(HttpStatus.NOT_FOUND, "파일이 존재하지 않습니다."),
    UNSUPPORTED_EXTENSION(HttpStatus.UNSUPPORTED_MEDIA_TYPE, "지원하지 않는 파일 형식입니다."),
    INVALID_FILE_REQUEST(HttpStatus.BAD_REQUEST, "잘못된 형식의 파일 요청입니다.");

    private final HttpStatus status;
    private final String message;

    ErrorCode(HttpStatus status, String message) {
        this.status = status;
        this.message = message;
    }

    public HttpStatus getStatusCode() {
        return status;
    }
}