package com.stockport.server.application.controller.IndexData.dto;

import com.stockport.server.domain.indexData.constant.MarketType;
import com.stockport.server.domain.indexData.entity.IndexData;
import lombok.Builder;
import lombok.Getter;

import java.math.BigDecimal;
import java.time.LocalDate;

@Getter
@Builder
public class IndexDataResponse {
    private MarketType marketType;   // 코스피/코스닥
    private LocalDate baseDate;      // 기준일
    private BigDecimal openPrice;    // 시가
    private BigDecimal closePrice;   // 종가
    private BigDecimal highPrice;    // 고가
    private BigDecimal lowPrice;     // 저가
    private BigDecimal changeAmount; // 등락폭
    private BigDecimal changeRate;   // 등락률

    public static IndexDataResponse of(IndexData indexData) {
        return IndexDataResponse.builder()
                .marketType(indexData.getMarketType())
                .baseDate(indexData.getBaseDate())
                .openPrice(indexData.getOpenPrice())
                .closePrice(indexData.getClosePrice())
                .highPrice(indexData.getHighPrice())
                .lowPrice(indexData.getLowPrice())
                .changeAmount(indexData.getChangeAmount())
                .changeRate(indexData.getChangeRate())
                .build();
    }
}