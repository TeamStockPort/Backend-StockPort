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

    @Column(nullable = false)
    private Integer openPrice;          // 시가

    @Column(nullable = false)
    private Integer currentPrice;       // 현재가

    @Column(nullable = false)
    private Integer highPrice;          // 고가

    @Column(nullable = false)
    private Integer lowPrice;           // 저가

    @Column(nullable = false)
    private Integer changeAmount;       // 등락폭

    @Column(precision = 5, scale = 2)
    private BigDecimal changeRate;      // 등락률

    public void updateStock(Stock stock) {
        this.stock = stock;
    }

    @Builder
    public StockCurrentPrice(Stock stock, LocalDate baseDate,
                             Integer openPrice, Integer currentPrice,
                             Integer highPrice, Integer lowPrice,
                             Integer changeAmount, BigDecimal changeRate) {
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
                                          Integer openPrice, Integer currentPrice,
                                          Integer highPrice, Integer lowPrice,
                                          Integer changeAmount, BigDecimal changeRate) {
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
