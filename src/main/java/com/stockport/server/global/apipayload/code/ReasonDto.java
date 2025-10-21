package com.stockport.server.global.apipayload.code;

import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import lombok.Builder;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@JsonPropertyOrder({"isSuccess", "httpStatus", "code", "message"})
public class ReasonDto {
    private final Boolean isSuccess;
    private final HttpStatus httpStatus;
    private final String code;
    private final String message;

    @Builder
    public ReasonDto(HttpStatus httpStatus, Boolean isSuccess, String code, String message) {
        this.httpStatus = httpStatus;
        this.isSuccess = isSuccess;
        this.code = code;
        this.message = message;
    }
}
