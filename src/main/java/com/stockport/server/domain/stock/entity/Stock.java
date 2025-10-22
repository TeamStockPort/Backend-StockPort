package com.stockport.server.domain.stock.entity;
import com.stockport.server.global.entity.BaseEntity;
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
public class Stock extends BaseEntity {
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

    @OneToOne(mappedBy = "stock", fetch = FetchType.EAGER, cascade = CascadeType.ALL, orphanRemoval = true)
    private StockCurrentPrice currentPriceInfo;     // 현재 주가 정보

    @OneToMany(mappedBy = "stock", fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
    private List<StockPrice> stockPrices;           // 과거 주가 정보

    public void updateCurrentPriceInfo(StockCurrentPrice newCurrentPriceInfo) {
        if (this.currentPriceInfo != null)
            this.currentPriceInfo.updateStock(null);

        this.currentPriceInfo = newCurrentPriceInfo;
        newCurrentPriceInfo.updateStock(this);

        updateMarketCap();
    }

    public void updateMarketCap() {
        this.marketCap = currentPriceInfo.getCurrentPrice() * this.listedShares;
    }

    @Builder
    public Stock(String isinCd, String stockCd, String stockName, Long marketCap, Long listedShares, LocalDate listedDate) {
        this.isinCd = isinCd;
        this.stockCd = stockCd;
        this.stockName = stockName;
        this.marketCap = marketCap;
        this.listedShares = listedShares;
        this.listedDate = listedDate;
    }

    public static Stock create(String isinCd, String stockCd, String stockName, Long marketCap, Long listedShares, LocalDate listedDate) {
        return Stock.builder()
                .isinCd(isinCd)
                .stockCd(stockCd)
                .stockName(stockName)
                .marketCap(marketCap)
                .listedShares(listedShares)
                .listedDate(listedDate)
                .build();
    }
}