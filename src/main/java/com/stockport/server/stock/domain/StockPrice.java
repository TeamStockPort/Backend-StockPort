package com.stockport.server.stock.domain;

import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;

@Entity
@Getter
@NoArgsConstructor(access = lombok.AccessLevel.PROTECTED)
public class StockPrice {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY) // Auto Increment
    @Column(name = "id", nullable = false)
    private Long id;

    @Column(name = "isinCd", length = 20, nullable = false)
    private String isinCd; // 종목 식별코드 (FK: Stock.isinCd)

    @Column(name = "basDt", nullable = false)
    private LocalDate basDt; // 기준일자

    @Column(name = "clpr")
    private Integer clpr; // 종가

    @Column(name = "mkp")
    private Integer mkp; // 시가

    @Column(name = "hipr")
    private Integer hipr; // 고가

    @Column(name = "lopr")
    private Integer lopr; // 저가

    @Column(name = "vs")
    private Integer vs; // 전일 대비 등락

    @Column(name = "fltRt", precision = 5, scale = 2)
    private BigDecimal fltRt; // 등락률 (소수 둘째 자리까지)

    @Column(name = "mrktTotAmt")
    private Long mrktTotAmt; // 시가총액

    @Builder
    private StockPrice(String isinCd, LocalDate basDt, Integer clpr, Integer mkp, Integer hipr, Integer lopr, Integer vs, BigDecimal fltRt, Long mrktTotAmt) {
        this.isinCd = isinCd;
        this.basDt = basDt;
        this.clpr = clpr;
        this.mkp = mkp;
        this.hipr = hipr;
        this.lopr = lopr;
        this.vs = vs;
        this.fltRt = fltRt;
        this.mrktTotAmt = mrktTotAmt;
    }

    public static StockPrice create(String isinCd, LocalDate basDt, Integer clpr, Integer mkp, Integer hipr, Integer lopr, Integer vs, BigDecimal fltRt, Long mrktTotAmt) {
        return StockPrice.builder()
                .isinCd(isinCd)
                .basDt(basDt)
                .clpr(clpr)
                .mkp(mkp)
                .hipr(hipr)
                .lopr(lopr)
                .vs(vs)
                .fltRt(fltRt)
                .mrktTotAmt(mrktTotAmt)
                .build();
    }
}