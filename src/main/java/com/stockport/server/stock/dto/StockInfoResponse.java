package com.stockport.server.stock.dto;

import com.stockport.server.stock.domain.Stock;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class
StockInfoResponse {
    private String basDt;
    private String srtnCd;
    private String isinCd;
    private String mrktCtg;
    private String itmsNm;
    private String crno;
    private String corpNm;
    private String lstgDt;

    @Builder
    public StockInfoResponse(String basDt, String srtnCd, String isinCd, String mrktCtg, String itmsNm, String crno, String corpNm, String lstgDt) {
        this.basDt = basDt;
        this.srtnCd = srtnCd;
        this.isinCd = isinCd;
        this.mrktCtg = mrktCtg;
        this.itmsNm = itmsNm;
        this.crno = crno;
        this.corpNm = corpNm;
        this.lstgDt = lstgDt;
    }

    public static Stock toEntity(StockInfoResponse dto) {
        return Stock.builder()
                .isinCd(dto.getIsinCd())
                .srtnCd(dto.getSrtnCd())
                .itmsNm(dto.getItmsNm())
                .mrktCtg(dto.getMrktCtg())
                .enpNm(dto.getCorpNm())
                .enpAbbrvNm(dto.getCorpNm())
                .lstgDt(LocalDate.parse(dto.getLstgDt(), DateTimeFormatter.BASIC_ISO_DATE))
                .build();
    }
}
