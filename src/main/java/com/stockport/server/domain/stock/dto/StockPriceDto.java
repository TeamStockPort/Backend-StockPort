package com.stockport.server.domain.stock.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.stockport.server.domain.stock.entity.StockPrice;
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
    private Integer clpr;         // 종가
    private Integer vs;           // 전일 대비 (등락 금액)
    private BigDecimal fltRt;     // 등락률 (소수 둘째 자리까지)
    private Integer hipr;         // 고가
    private Integer lopr;         // 저가
    private Integer mkp;          // 시가

    @Builder
    public StockPriceDto(String isinCd, String basDt, Integer clpr, Integer vs,
                         BigDecimal fltRt, Integer hipr, Integer lopr, Integer mkp) {
        this.isinCd = isinCd;
        this.basDt = basDt;
        this.clpr = clpr;
        this.vs = vs;
        this.fltRt = fltRt;
        this.hipr = hipr;
        this.lopr = lopr;
        this.mkp = mkp;
    }

    public static StockPrice toEntity(StockPriceDto dto) {
        return StockPrice.builder()
                .baseDate(LocalDate.parse(dto.getBasDt(), DateTimeFormatter.ofPattern("yyyyMMdd")))
                .openPrice(dto.getMkp())
                .closePrice(dto.getClpr())
                .highPrice(dto.getHipr())
                .lowPrice(dto.getLopr())
                .changeAmount(dto.getVs())
                .changeRate(dto.getFltRt())
                .build();
    }
}