package com.stockport.server.application.controller.stock.dto;

import com.stockport.server.domain.indexData.constant.MarketType;
import com.stockport.server.domain.stock.entity.Stock;
import lombok.Builder;
import lombok.Getter;
import lombok.experimental.SuperBuilder;

import java.time.LocalDate;
import java.util.List;

@Getter
@SuperBuilder
public class StockInfoResponse {
    private String stockName;
    private String stockCode;
    private String isinCode;
    private LocalDate listedDate;
    private Long listedShared;
    private Long marketCap;
    private List<StockPriceResponse> stockPriceList;

    public static StockInfoResponse of(Stock stock, List<StockPriceResponse> stockPriceList) {
        return StockInfoResponse.builder()
                .stockName(stock.getStockName())
                .stockCode(stock.getStockCd())
                .isinCode(stock.getIsinCd())
                .listedDate(stock.getListedDate())
                .listedShared(stock.getListedShares())
                .marketCap(stock.getMarketCap())
                .stockPriceList(stockPriceList)
                .build();
    }
}
