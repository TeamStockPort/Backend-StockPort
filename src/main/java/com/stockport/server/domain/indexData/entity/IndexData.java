package com.stockport.server.domain.indexData.entity;

import com.stockport.server.domain.indexData.constant.MarketType;
import com.stockport.server.global.entity.BaseEntity;
import com.stockport.server.global.feign.dto.KisIndexCurrentPrice;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Builder;
import lombok.AllArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
@Table(
        name = "index_data",
        uniqueConstraints = {
                @UniqueConstraint(name = "uk_index_code_date", columnNames = {"marketType", "baseDate"})
        }
)
public class IndexData extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    private MarketType marketType; // 코스피/코스닥 구분

    @Column(nullable = false)
    private LocalDate baseDate; // 기준일

    @Column(precision = 10, scale = 2)
    private BigDecimal openPrice; // 시가

    @Column(precision = 10, scale = 2)
    private BigDecimal closePrice; // 종가

    @Column(precision = 10, scale = 2)
    private BigDecimal highPrice; // 고가

    @Column(precision = 10, scale = 2)
    private BigDecimal lowPrice; // 저가

    @Column(precision = 10, scale = 2)
    private BigDecimal changeAmount; // 등락폭

    @Column(precision = 5, scale = 2)
    private BigDecimal changeRate; // 등락률

    public IndexData updateMarketType(MarketType marketType) {
        this.marketType = marketType;
        return this;
    }

    public IndexData updatePrice(IndexData indexData) {
        this.openPrice = indexData.getOpenPrice();
        this.closePrice = indexData.getClosePrice();
        this.highPrice = indexData.getHighPrice();
        this.lowPrice = indexData.getLowPrice();
        this.changeAmount = indexData.getChangeAmount();
        this.changeRate = indexData.getChangeRate();
        return this;
    }

    public IndexData updateClosePrice(BigDecimal currentPrice) {
        this.closePrice = currentPrice;
        return this;
    }

    public IndexData updateBaseDate(LocalDate baseDate) {
        this.baseDate = baseDate;
        return this;
    }
}