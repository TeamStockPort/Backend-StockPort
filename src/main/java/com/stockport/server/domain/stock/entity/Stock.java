package com.stockport.server.domain.stock.entity;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.List;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Stock {
    @Id
    @Column(name = "ISIN_CD", length = 20, nullable = false)
    private String isinCd;   // 종목 식별 고유번호 (PK)

    @Column(name = "STOCK_CD", length = 9, nullable = false)
    private String stockCd;  // 단축코드

    @Column(name = "STOCK_NAME", length = 100, nullable = false)
    private String name;   // 종목명

    @Column(name = "MARKET_CAP", nullable = false)
    private Long marketCap; // 시가총액

    @Column(name = "LISTED_DATE", nullable = false)
    private LocalDate listedDate; // 상장일

    @Column(name = "LISTED_SHARES", nullable = false)
    private Long listedShares; // 상장주식수

    @OneToMany(mappedBy = "stock", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<StockPrice> stockPrices; // StockPrice 엔티티와의 일대다 관계

    public void updateMarketCap() {
        if (stockPrices == null || stockPrices.isEmpty()) {
            return;
        }

        StockPrice latestPrice = stockPrices.stream()
                .max((sp1, sp2) -> sp1.getBasDt().compareTo(sp2.getBasDt()))
                .orElse(null);

        this.marketCap = latestPrice.getClpr().longValue() * this.listedShares;
    }

    @Builder
    public Stock(String isinCd, String stockCd, String name, Long marketCap, Long listedShares, LocalDate listedDate) {
        this.isinCd = isinCd;
        this.stockCd = stockCd;
        this.name = name;
        this.marketCap = marketCap;
        this.listedShares = listedShares;
        this.listedDate = listedDate;
    }

    public static Stock create(String isinCd, String stockCd, String name, Long marketCap, Long listedShares, LocalDate listedDate) {
        return Stock.builder()
                .isinCd(isinCd)
                .stockCd(stockCd)
                .name(name)
                .marketCap(marketCap)
                .listedShares(listedShares)
                .listedDate(listedDate)
                .build();
    }
}