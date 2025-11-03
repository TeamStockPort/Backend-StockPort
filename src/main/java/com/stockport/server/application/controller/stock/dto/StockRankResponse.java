package com.stockport.server.application.controller.stock.dto;

import com.stockport.server.domain.stock.entity.Stock;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

import java.util.List;

@Getter
@Setter
@SuperBuilder
public class StockRankResponse extends StockInfoResponse {
    private Integer rank;

    public static StockRankResponse of(Stock stock, Integer rank, List<StockPriceResponse> stockPriceList) {
        return StockRankResponse.builder()
                .stockName(stock.getStockName())
                .stockCode(stock.getStockCd())
                .isinCode(stock.getIsinCd())
                .listedDate(stock.getListedDate())
                .listedShared(stock.getListedShares())
                .marketCap(stock.getMarketCap())
                .stockPriceList(stockPriceList)
                .rank(rank)
                .build();
    }
}
