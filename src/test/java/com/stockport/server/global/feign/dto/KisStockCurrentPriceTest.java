package com.stockport.server.global.feign.dto;

import com.stockport.server.IntegrationTestSupport;
import com.stockport.server.domain.stock.entity.StockCurrentPrice;
import com.stockport.server.global.exception.GeneralException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDate;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class KisStockCurrentPriceTest extends IntegrationTestSupport {

    @Test
    @DisplayName("toEntity: DTO를 StockCurrentPrice 엔티티로 정상 변환한다")
    void givenValidDto_whenToEntity_thenReturnStockCurrentPriceEntity() {
        // given
        KisStockCurrentPrice dto = KisStockCurrentPrice.create(
                "1000",      // 시가
                "1100",      // 현재가
                "1150",      // 고가
                "950",       // 저가
                "100",       // 등락폭
                "10.00"      // 등락률
        );

        // when
        StockCurrentPrice entity = dto.toEntity();

        // then
        assertThat(entity).isNotNull();
        assertThat(entity.getOpenPrice()).isEqualTo(1000);
        assertThat(entity.getCurrentPrice()).isEqualTo(1100);
        assertThat(entity.getHighPrice()).isEqualTo(1150);
        assertThat(entity.getLowPrice()).isEqualTo(950);
        assertThat(entity.getChangeAmount()).isEqualTo(100);
        assertThat(entity.getChangeRate()).isEqualByComparingTo(BigDecimal.valueOf(10.00));
    }
}