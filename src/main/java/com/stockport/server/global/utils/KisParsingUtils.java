package com.stockport.server.global.utils;

import com.stockport.server.global.apipayload.code.status.ErrorStatus;
import com.stockport.server.global.exception.GeneralException;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public final class KisParsingUtils {

    private KisParsingUtils() {} // 인스턴스화 방지

    public static BigDecimal parseBigDecimalSafe(String val) {
        if (val == null || val.isEmpty())
            return BigDecimal.ZERO;

        try {
            return new BigDecimal(val);
        } catch (Exception e) {
            throw new GeneralException(ErrorStatus.PARSE_ERROR);
        }
    }

    public static LocalDate parseDateSafe(String val) {
        try {
            return LocalDate.parse(val, DateTimeFormatter.ofPattern("yyyyMMdd"));
        } catch (Exception e) {
            throw new GeneralException(ErrorStatus.PARSE_ERROR);
        }
    }
}