package com.stockport.server.global.feign.dto;

import com.stockport.server.domain.indexData.entity.IndexData;
import com.stockport.server.global.exception.GeneralException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

class KisIndexPeriodPriceTest {

    @Test
    @DisplayName("toEntity(): 문자열 필드가 올바르게 파싱되고 등락/등락률이 계산된다")
    void toEntity_shouldMapFieldsAndCalculateChangeAndRate() {
        // given
        KisIndexPeriodPrice dto = KisIndexPeriodPrice.builder()
                .baseDate("20251028")      // YYYYMMDD
                .closePrice("3300.00")
                .openPrice("3290.11")
                .highPrice("3312.55")
                .lowPrice("3280.10")
                .build();

        BigDecimal prevClosePrice = new BigDecimal("3290.11");

        // when
        IndexData entity = dto.toEntity(prevClosePrice);

        // then
        assertNotNull(entity, "엔티티는 null이면 안 됨");

        // 날짜 파싱 검증
        assertEquals(LocalDate.of(2025, 10, 28), entity.getBaseDate(), "baseDate 파싱 검증");

        // 가격 필드 파싱 검증
        assertEquals(new BigDecimal("3300.00"), entity.getClosePrice(), "closePrice 매핑");
        assertEquals(new BigDecimal("3290.11"), entity.getOpenPrice(),  "openPrice 매핑");
        assertEquals(new BigDecimal("3312.55"), entity.getHighPrice(),  "highPrice 매핑");
        assertEquals(new BigDecimal("3280.10"), entity.getLowPrice(),   "lowPrice 매핑");

        // 등락/등락률 계산 검증 (등락률 소수점 둘째 자리 반올림)
        // changeAmount = 3300.00 - 3290.11 = 9.89
        // changeRate   = 9.89 * 100 / 3290.11 ≈ 0.3005... -> 0.30 (HALF_UP로 둘째 자리)
        assertEquals(new BigDecimal("9.89"), entity.getChangeAmount(), "changeAmount 계산");
        assertEquals(new BigDecimal("0.30"), entity.getChangeRate(),   "changeRate 계산(스케일 2자리)");
    }

    @Test
    @DisplayName("toEntity(): 이전 종가가 0 등 예외 상황이면 GeneralException(PARSE_ERROR) 발생")
    void toEntity_shouldThrowWhenPrevClosePriceInvalid() {
        // given
        KisIndexPeriodPrice dto = KisIndexPeriodPrice.builder()
                .baseDate("20251028")
                .closePrice("100.00")
                .openPrice("100.00")
                .highPrice("101.00")
                .lowPrice("99.00")
                .build();

        BigDecimal prevClosePriceZero = new BigDecimal("0.00");

        // when & then
        assertThrows(GeneralException.class, () -> dto.toEntity(prevClosePriceZero),
                "이전 종가가 0이면 나눗셈 예외 -> GeneralException 이어야 함");
    }
}