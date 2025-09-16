package com.stockport.server.stock.domain;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Stock {
    @Id
    @Column(name = "isinCd", length = 20, nullable = false)
    private String isinCd;   // 종목 식별 고유번호 (PK)

    @Column(name = "srtnCd", length = 9, nullable = false)
    private String srtnCd;   // 한국거래소 종목코드

    @Column(name = "itmsNm", length = 240, nullable = false)
    private String itmsNm;   // 종목명

    @Column(name = "mrktCtg", length = 10, nullable = false)
    private String mrktCtg;  // 시장 구분 (KOSPI/KOSDAQ/KONEX)

    @Column(name = "enpNm", length = 240)
    private String enpNm;    // 회사명

    @Column(name = "enpAbbrvNm", length = 240)
    private String enpAbbrvNm; // 회사명(약칭)

    @OneToMany(mappedBy = "stock", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<StockPrice> stockPrices; // StockPrice 엔티티와의 일대다 관계

    @Builder
    private Stock(String isinCd, String srtnCd, String itmsNm, String mrktCtg, String enpNm, String enpAbbrvNm) {
        this.isinCd = isinCd;
        this.srtnCd = srtnCd;
        this.itmsNm = itmsNm;
        this.mrktCtg = mrktCtg;
        this.enpNm = enpNm;
        this.enpAbbrvNm = enpAbbrvNm;
    }

    public static Stock create(String isinCd, String srtnCd, String itmsNm, String mrktCtg, String enpNm, String enpAbbrvNm) {
        return Stock.builder()
                .isinCd(isinCd)
                .srtnCd(srtnCd)
                .itmsNm(itmsNm)
                .mrktCtg(mrktCtg)
                .enpNm(enpNm)
                .enpAbbrvNm(enpAbbrvNm)
                .build();
    }
}