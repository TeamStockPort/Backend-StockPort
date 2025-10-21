package com.stockport.server.global.exception;

import com.stockport.server.global.apipayload.code.ReasonDto;
import com.stockport.server.global.apipayload.code.status.ErrorStatus;
import lombok.Getter;

@Getter
public class GeneralException extends RuntimeException {
    private final ErrorStatus errorStatus;

    public GeneralException(ErrorStatus errorStatus) {
        super(errorStatus.getMessage());
        this.errorStatus = errorStatus;
    }

    public ReasonDto getErrorStatus() {
        return this.errorStatus.getReason();
    }
}
