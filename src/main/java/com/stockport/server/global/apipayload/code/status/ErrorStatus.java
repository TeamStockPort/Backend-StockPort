package com.stockport.server.global.apipayload.code.status;

import com.stockport.server.global.apipayload.code.BaseCode;
import com.stockport.server.global.apipayload.code.ReasonDto;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
public enum ErrorStatus implements BaseCode {
    METHOD_NOT_ALLOWED(HttpStatus.METHOD_NOT_ALLOWED,"METHOD405", "허용되지 않은 HTTP 메서드입니다."),

    PARSE_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "PARSE5001", "데이터 파싱 중 오류가 발생했습니다."),

    FEIGN_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "FEIGN5001", "외부 API 호출에 실패하였습니다.");

    private final HttpStatus httpStatus;
    private final String code;
    private final String message;

    @Override
    public ReasonDto getReason() {
        return ReasonDto.builder()
                .isSuccess(false)
                .httpStatus(httpStatus)
                .code(code)
                .message(message)
                .build();
    }
}
