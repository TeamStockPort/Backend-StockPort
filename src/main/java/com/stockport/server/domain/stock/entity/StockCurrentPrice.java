package com.stockport.server.domain.stock.entity;

import com.stockport.server.global.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;

import static jakarta.persistence.FetchType.LAZY;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class StockCurrentPrice extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(fetch = LAZY)
    @JoinColumn(name = "isin_cd", nullable = false)
    private Stock stock;

    @Column(nullable = false)
    private LocalDate baseDate;         // 기준일

    @Column(precision = 12, scale = 2)
    private BigDecimal openPrice;          // 시가

    @Column(precision = 12, scale = 2)
    private BigDecimal currentPrice;       // 현재가

    @Column(precision = 12, scale = 2)
    private BigDecimal highPrice;          // 고가

    @Column(precision = 12, scale = 2)
    private BigDecimal lowPrice;           // 저가

    @Column(precision = 12, scale = 2)
    private BigDecimal changeAmount;       // 등락폭

    @Column(precision = 5, scale = 2)
    private BigDecimal changeRate;      // 등락률

    public void updateStock(Stock stock) {
        this.stock = stock;
    }

    public void updateCurrentPrice(StockCurrentPrice newCurrentPrice) {
        this.baseDate = newCurrentPrice.getBaseDate();
        this.openPrice = newCurrentPrice.getOpenPrice();
        this.currentPrice = newCurrentPrice.getCurrentPrice();
        this.highPrice = newCurrentPrice.getHighPrice();
        this.lowPrice = newCurrentPrice.getLowPrice();
        this.changeAmount = newCurrentPrice.getChangeAmount();
        this.changeRate = newCurrentPrice.getChangeRate();
    }

    @Builder
    public StockCurrentPrice(Stock stock, LocalDate baseDate,
                             BigDecimal openPrice, BigDecimal currentPrice,
                             BigDecimal highPrice, BigDecimal lowPrice,
                             BigDecimal changeAmount, BigDecimal changeRate) {
        this.stock = stock;
        this.baseDate = baseDate;
        this.openPrice = openPrice;
        this.currentPrice = currentPrice;
        this.highPrice = highPrice;
        this.lowPrice = lowPrice;
        this.changeAmount = changeAmount;
        this.changeRate = changeRate;
    }

    public static StockCurrentPrice create(LocalDate baseDate,
                                           BigDecimal openPrice, BigDecimal currentPrice,
                                           BigDecimal highPrice, BigDecimal lowPrice,
                                           BigDecimal changeAmount, BigDecimal changeRate) {
        return StockCurrentPrice.builder()
                .baseDate(baseDate)
                .openPrice(openPrice)
                .currentPrice(currentPrice)
                .highPrice(highPrice)
                .lowPrice(lowPrice)
                .changeAmount(changeAmount)
                .changeRate(changeRate)
                .build();
    }

    public StockPrice toStockPriceEntity() {
        return StockPrice.builder()
                .baseDate(this.baseDate)
                .openPrice(this.openPrice)
                .closePrice(this.currentPrice)
                .highPrice(this.highPrice)
                .lowPrice(this.lowPrice)
                .changeAmount(this.changeAmount)
                .changeRate(this.changeRate)
                .build();
    }
}
