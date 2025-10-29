package com.stockport.server.domain.stock.entity;

import com.stockport.server.IntegrationTestSupport;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDate;

import static org.assertj.core.api.Assertions.assertThat;

class StockTest extends IntegrationTestSupport {

    @Test
    @DisplayName("updateCurrentPriceInfo: 새로운 현재가 정보를 적용하면 기존 정보가 교체되고 시가총액이 갱신된다")
    void givenNewCurrentPriceInfo_whenUpdateCurrentPriceInfo_thenReplaceAndUpdateMarketCap() {
        // given
        Stock stock = Stock.create(
                "KR7005930003",
                "005930",
                "삼성전자",
                0L,
                100000000L,  // 상장주식수
                LocalDate.of(1975, 6, 11)
        );

        StockCurrentPrice oldPrice = StockCurrentPrice.builder()
                .stock(stock)
                .currentPrice(70000)
                .openPrice(69000)
                .highPrice(70500)
                .lowPrice(68000)
                .changeAmount(500)
                .changeRate(BigDecimal.valueOf(0.7))
                .baseDate(LocalDate.of(2025, 10, 20))
                .build();

        StockCurrentPrice newPrice = StockCurrentPrice.builder()
                .stock(stock)
                .currentPrice(72000)
                .openPrice(71000)
                .highPrice(73000)
                .lowPrice(70000)
                .changeAmount(2000)
                .changeRate(BigDecimal.valueOf(2.8))
                .baseDate(LocalDate.of(2025, 10, 21))
                .build();

        stock.updateCurrentPriceInfo(oldPrice);

        // when
        stock.updateCurrentPriceInfo(newPrice);

        // then
        assertThat(stock.getCurrentPriceInfo()).isEqualTo(newPrice);
        assertThat(newPrice.getStock()).isEqualTo(stock);
        assertThat(stock.getMarketCap()).isEqualTo(72000L * 100000000L);
        assertThat(oldPrice.getStock()).isNull(); // 이전 객체는 연결 해제됨
    }

    @Test
    @DisplayName("updateMarketCap: 현재가와 상장주식수를 기반으로 시가총액이 정확히 계산된다")
    void givenCurrentPrice_whenUpdateMarketCap_thenMarketCapRecalculated() {
        // given
        Stock stock = Stock.create(
                "KR7005930003",
                "005930",
                "삼성전자",
                0L,
                100000000L,
                LocalDate.of(1975, 6, 11)
        );

        StockCurrentPrice priceInfo = StockCurrentPrice.builder()
                .stock(stock)
                .currentPrice(75000)
                .openPrice(74000)
                .highPrice(75500)
                .lowPrice(73000)
                .changeAmount(1000)
                .changeRate(BigDecimal.valueOf(1.35))
                .baseDate(LocalDate.of(2025, 10, 22))
                .build();

        stock.updateCurrentPriceInfo(priceInfo);

        // when
        stock.updateMarketCap();

        // then
        assertThat(stock.getMarketCap()).isEqualTo(75000L * 100000000L);
    }
}