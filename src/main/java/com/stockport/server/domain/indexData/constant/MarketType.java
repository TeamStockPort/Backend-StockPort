package com.stockport.server.domain.indexData.constant;

import com.stockport.server.global.apipayload.code.status.ErrorStatus;
import com.stockport.server.global.exception.GeneralException;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum MarketType {
    KOSPI("0001"),   // 코스피
    KOSDAQ("1001");  // 코스닥

    public static MarketType fromCode(String code) {
        for (MarketType type : values()) {
            if (type.code.equals(code)) {
                return type;
            }
        }
        throw new GeneralException(ErrorStatus.MARKET_CODE_ERROR);
    }

    private final String code;
}