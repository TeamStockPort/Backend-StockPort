package com.stockport.server.global.feign.dto;

import com.stockport.server.IntegrationTestSupport;
import com.stockport.server.domain.indexData.constant.MarketType;
import com.stockport.server.domain.indexData.entity.IndexData;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

class KisIndexCurrentPriceTest extends IntegrationTestSupport {
        @Test
        @DisplayName("toEntity(): 가격/등락/날짜/마켓타입이 올바르게 매핑된다")
        void toEntity_shouldMapAllFields() {
            // given: 테스트 픽스처 (실제 DTO의 필드/빌더/세터 이름에 맞게 수정)
            KisIndexCurrentPrice dto = KisIndexCurrentPrice.builder()
                    // ▼ DTO의 실제 타입/이름에 맞게 넣어 주세요
                    .openPrice("3290.11")
                    .highPrice("3312.55")
                    .lowPrice("3280.10")
                    .currentPrice("3300.00")
                    .changeSign("+")
                    .changeAmount("9.89")
                    .changeRate("0.30")
                    .build();

            MarketType marketType = MarketType.KOSPI;

            // when
            IndexData entity = dto.toEntity(marketType, LocalDate.of(2025, 10, 28));

            // then
            assertNotNull(entity, "toEntity()는 null을 반환하면 안 된다.");
            assertEquals(LocalDate.of(2025, 10, 28), entity.getBaseDate(), "baseDate 매핑");
            assertEquals(marketType, entity.getMarketType(), "marketType 매핑");

            // 가격 필드 매핑
            assertEquals(new BigDecimal("3290.11"), entity.getOpenPrice(), "openPrice 매핑");
            assertEquals(new BigDecimal("3312.55"), entity.getHighPrice(), "highPrice 매핑");
            assertEquals(new BigDecimal("3280.10"), entity.getLowPrice(), "lowPrice 매핑");
            assertEquals(new BigDecimal("3300.00"), entity.getClosePrice(), "closePrice 매핑");

            // 등락 정보 매핑
            assertEquals(new BigDecimal("9.89"), entity.getChangeAmount(), "changeAmount 매핑");
            assertEquals(new BigDecimal("0.30"), entity.getChangeRate(), "changeRate 매핑");
        }
}