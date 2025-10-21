package com.stockport.server.domain.stock.entity;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Stock {
    @Id
    @Column(length = 20, nullable = false)
    private String isinCd;              // 종목 식별 고유번호

    @Column(length = 9, nullable = false)
    private String stockCd;             // 단축코드

    @Column(length = 100, nullable = false)
    private String stockName;           // 종목명

    @Column(nullable = false)
    private Long marketCap;             // 시가총액

    @Column(nullable = false)
    private LocalDate listedDate;       // 상장일

    @Column(nullable = false)
    private Long listedShares;          // 상장주식수

    @Column(nullable = false)
    private Integer curPrice;           // 현재가

    @Column(nullable = false)
    private LocalDateTime lastUpdate;   // 마지막 조회 시간

    @OneToMany(mappedBy = "stock", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<StockPrice> stockPrices; // StockPrice 엔티티와의 일대다 관계

    public Stock updateCurPrice(Integer curPrice) {
        this.curPrice = curPrice;
        updateMarketCap();
        updateTime();
        return this;
    }

    public void updateMarketCap() {
        this.marketCap = curPrice * this.listedShares;
    }

    public void updateTime() {
        this.lastUpdate = LocalDateTime.now();
    }

    @Builder
    public Stock(String isinCd, String stockCd, String stockName, Long marketCap, Long listedShares, LocalDate listedDate, Integer curPrice) {
        this.isinCd = isinCd;
        this.stockCd = stockCd;
        this.stockName = stockName;
        this.marketCap = marketCap;
        this.listedShares = listedShares;
        this.listedDate = listedDate;
        this.curPrice = curPrice;
    }

    public static Stock create(String isinCd, String stockCd, String stockName, Long marketCap, Long listedShares, LocalDate listedDate, Integer curPrice) {
        return Stock.builder()
                .isinCd(isinCd)
                .stockCd(stockCd)
                .stockName(stockName)
                .marketCap(marketCap)
                .listedShares(listedShares)
                .listedDate(listedDate)
                .curPrice(curPrice)
                .build();
    }
}