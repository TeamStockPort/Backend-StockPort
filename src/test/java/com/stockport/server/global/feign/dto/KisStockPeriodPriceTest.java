package com.stockport.server.global.feign.dto;

import com.stockport.server.domain.stock.entity.StockPrice;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;

import static org.assertj.core.api.Assertions.assertThat;

class KisStockPeriodPriceTest {

    @Test
    @DisplayName("toEntity: DTO의 데이터를 기반으로 StockPrice 엔티티가 정상 생성된다")
    void givenValidDto_whenToEntity_thenReturnValidStockPriceEntity() {
        // given
        KisStockPeriodPrice dto = KisStockPeriodPrice.create(
                "20241021",  // 기준일
                "1000",      // 시가
                "1100",      // 종가
                "1150",      // 고가
                "950",       // 저가
                "+",         // 등락부호
                "100"        // 등락폭
        );


        // when
        StockPrice entity = dto.toEntity();

        // then
        assertThat(entity).isNotNull();
        assertThat(entity.getBaseDate()).isEqualTo(LocalDate.of(2024, 10, 21));
        assertThat(entity.getOpenPrice()).isEqualTo(1000);
        assertThat(entity.getClosePrice()).isEqualTo(1100);
        assertThat(entity.getHighPrice()).isEqualTo(1150);
        assertThat(entity.getLowPrice()).isEqualTo(950);
        assertThat(entity.getChangeAmount()).isEqualTo(100);
        assertThat(entity.getChangeRate()).isEqualByComparingTo(BigDecimal.valueOf(10.00).setScale(2, RoundingMode.HALF_UP));
    }
}