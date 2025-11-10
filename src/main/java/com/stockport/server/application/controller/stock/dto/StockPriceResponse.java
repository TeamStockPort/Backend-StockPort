package com.stockport.server.application.controller.stock.dto;

import com.stockport.server.domain.stock.entity.StockCurrentPrice;
import com.stockport.server.domain.stock.entity.StockPrice;
import lombok.Builder;
import lombok.Getter;

import java.math.BigDecimal;
import java.time.LocalDate;

@Getter
@Builder
public class StockPriceResponse {
    private LocalDate baseDate;
    private Integer openPrice;
    private Integer highPrice;
    private Integer lowPrice;
    private Integer closePrice;
    private Integer changeAmount;
    private BigDecimal changeRate;

    public static StockPriceResponse of(StockCurrentPrice stockCurrentPrice) {
        return StockPriceResponse.builder()
                .baseDate(stockCurrentPrice.getBaseDate())
                .openPrice(stockCurrentPrice.getOpenPrice())
                .highPrice(stockCurrentPrice.getHighPrice())
                .lowPrice(stockCurrentPrice.getLowPrice())
                .closePrice(stockCurrentPrice.getCurrentPrice())
                .changeAmount(stockCurrentPrice.getChangeAmount())
                .changeRate(stockCurrentPrice.getChangeRate())
                .build();
    }

    public static StockPriceResponse of (StockPrice stockPrice) {
        return StockPriceResponse.builder()
                .baseDate(stockPrice.getBaseDate())
                .openPrice(stockPrice.getOpenPrice())
                .highPrice(stockPrice.getHighPrice())
                .lowPrice(stockPrice.getLowPrice())
                .closePrice(stockPrice.getClosePrice())
                .changeAmount(stockPrice.getChangeAmount())
                .changeRate(stockPrice.getChangeRate())
                .build();
    }
}

