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
@Table(
        name = "stock_price",
        uniqueConstraints = {
                @UniqueConstraint(name = "uk_stock_price_stock_base_date", columnNames = {"isin_cd", "base_date"})
        },
        indexes = {
                @Index(name = "idx_stock_price_stock_base_date", columnList = "isin_cd, base_date")
        }
)
public class StockPrice extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = "isin_cd", nullable = false)
    private Stock stock;

    @Column(precision = 12, scale = 2)
    private LocalDate baseDate; // 기준일

    @Column(precision = 12, scale = 2)
    private BigDecimal openPrice;  // 시가

    @Column(precision = 12, scale = 2)
    private BigDecimal closePrice; // 종가

    @Column(precision = 12, scale = 2)
    private BigDecimal highPrice;  // 고가

    @Column(precision = 12, scale = 2)
    private BigDecimal lowPrice;   // 저가

    @Column(precision = 12, scale = 2)
    private BigDecimal changeAmount;   // 등락폭

    @Column(precision = 7, scale = 2)
    private BigDecimal changeRate;  // 등락률

    public StockPrice updateStock(Stock stock) {
        this.stock = stock;
        return this;
    }

    @Builder
    public StockPrice(Stock stock, LocalDate baseDate,
                      BigDecimal openPrice, BigDecimal closePrice, BigDecimal highPrice,
                      BigDecimal lowPrice, BigDecimal changeAmount, BigDecimal changeRate) {
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
                                    BigDecimal closePrice, BigDecimal openPrice, BigDecimal highPrice,
                                    BigDecimal lowPrice, BigDecimal changeAmount, BigDecimal changeRate) {
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