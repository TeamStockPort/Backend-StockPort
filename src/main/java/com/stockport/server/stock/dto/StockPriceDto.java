package com.stockport.server.stock.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.stockport.server.stock.domain.Stock;
import com.stockport.server.stock.domain.StockPrice;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@JsonIgnoreProperties(ignoreUnknown = true)
public class StockPriceDto {
    private String isinCd;        // 종목코드 (국제증권식별번호)
    private String basDt;         // 기준일자 (YYYYMMDD)
    private Long mrktTotAmt;      // 시가총액
    private Integer clpr;         // 종가
    private Integer vs;           // 전일 대비 (등락 금액)
    private BigDecimal fltRt;     // 등락률 (소수 둘째 자리까지)
    private Integer hipr;         // 고가
    private Integer lopr;         // 저가
    private Integer mkp;          // 시가
    private Long trqu;     // 거래량
    private Long trPrc;      // 거래대금
    private Long lstgStCnt;    // 상장주식수

    @Builder
    public StockPriceDto(String isinCd, String basDt, Long mrktTotAmt, Integer clpr, Integer vs, BigDecimal fltRt, Integer hipr, Integer lopr, Integer mkp, Long trqu, Long trPrc, Long lstgStCnt) {
        this.isinCd = isinCd;
        this.basDt = basDt;
        this.mrktTotAmt = mrktTotAmt;
        this.clpr = clpr;
        this.vs = vs;
        this.fltRt = fltRt;
        this.hipr = hipr;
        this.lopr = lopr;
        this.mkp = mkp;
        this.trqu = trqu;
        this.trPrc = trPrc;
        this.lstgStCnt = lstgStCnt;
    }

    public static StockPrice toEntity(StockPriceDto dto, Stock stock) {
        return StockPrice.builder()
                .stock(stock)
                .basDt(LocalDate.parse(dto.getBasDt(), DateTimeFormatter.ofPattern("yyyyMMdd")))
                .mrktTotAmt(dto.getMrktTotAmt())
                .clpr(dto.getClpr())
                .vs(dto.getVs())
                .fltRt(dto.getFltRt())
                .hipr(dto.getHipr())
                .lopr(dto.getLopr())
                .mkp(dto.getMkp())
                .trqu(dto.getTrqu())
                .trPrc(dto.getTrPrc())
                .lstgStCnt(dto.getLstgStCnt())
                .build();
    }


}