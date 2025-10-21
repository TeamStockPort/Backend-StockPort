package com.stockport.server.domain.stock.dto;

import com.stockport.server.domain.stock.entity.Stock;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class StockInfoDto {
    private String isinCd;        // 종목 식별 고유번호
    private String stockCd;       // 단축코드
    private String name;          // 종목명
    private Long marketCap;       // 시가총액
    private Long listedShares;    // 상장주식수
    private LocalDate listedDate; // 상장일

    public static StockInfoDto fromEntity(Stock stock) {
        return StockInfoDto.builder()
                .isinCd(stock.getIsinCd())
                .stockCd(stock.getStockCd())
                .name(stock.getStockName())
                .marketCap(stock.getMarketCap())
                .listedShares(stock.getListedShares())
                .listedDate(stock.getListedDate())
                .build();
    }

    public Stock toEntity() {
        return Stock.builder()
                .isinCd(this.isinCd)
                .stockCd(this.stockCd)
                .name(this.name)
                .marketCap(this.marketCap)
                .listedShares(this.listedShares)
                .listedDate(this.listedDate)
                .build();
    }
}
