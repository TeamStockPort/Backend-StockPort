package com.stockport.server.application.controller.stock.dto;

import com.stockport.server.domain.stock.entity.Stock;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class StockQueryResponse {
    private String stockName;
    private String stockCode;
    private String isinCode;

    public static StockQueryResponse of(Stock stock) {
        return StockQueryResponse.builder()
                .stockName(stock.getStockName())
                .stockCode(stock.getStockCd())
                .isinCode(stock.getIsinCd())
                .build();
    }
}
