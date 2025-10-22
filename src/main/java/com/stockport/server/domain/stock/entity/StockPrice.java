package com.stockport.server.domain.stock.entity;

import com.stockport.server.global.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDate;

import static jakarta.persistence.FetchType.LAZY;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class StockPrice extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = "stock_id", nullable = false)
    private Stock stock;

    @Column(nullable = false)
    private LocalDate baseDate; // 기준일

    @Column(nullable = false)
    private Integer openPrice;  // 시가

    @Column(nullable = false)
    private Integer closePrice; // 종가

    @Column(nullable = false)
    private Integer highPrice;  // 고가

    @Column(nullable = false)
    private Integer lowPrice;   // 저가

    @Column(nullable = false)
    private Integer changeAmount;   // 등락폭

    @Column(precision = 5, scale = 2)
    private BigDecimal changeRate;  // 등락률

    @Builder
    public StockPrice(Stock stock, LocalDate baseDate,
                      Integer openPrice, Integer closePrice, Integer highPrice,
                      Integer lowPrice, Integer changeAmount, BigDecimal changeRate) {
        this.stock = stock;
        this.baseDate = baseDate;
        this.openPrice = openPrice;
        this.closePrice = closePrice;
        this.highPrice = highPrice;
        this.lowPrice = lowPrice;
        this.changeAmount = changeAmount;
        this.changeRate = changeRate;
    }

    public static StockPrice create(Stock stock, LocalDate baseDate,
                                Integer closePrice, Integer openPrice, Integer highPrice,
                                Integer lowPrice, Integer changeAmount, BigDecimal changeRate) {
        return StockPrice.builder()
                .stock(stock)
                .baseDate(baseDate)
                .openPrice(openPrice)
                .closePrice(closePrice)
                .highPrice(highPrice)
                .lowPrice(lowPrice)
                .changeAmount(changeAmount)
                .changeRate(changeRate)
                .build();
    }
}