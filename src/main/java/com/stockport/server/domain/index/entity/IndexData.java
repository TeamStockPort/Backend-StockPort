package com.stockport.server.domain.index.entity;

import com.stockport.server.domain.index.constant.MarketType;
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
                @UniqueConstraint(name = "uk_index_code_date", columnNames = {"indexCode", "baseDate"})
        }
)
public class IndexData {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    private MarketType indexCode; // 코스피/코스닥 구분

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

    public void updateIndexCode(MarketType indexCode) {
        this.indexCode = indexCode;
    }
}