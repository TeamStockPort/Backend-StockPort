package com.stockport.server.stock.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.stockport.server.stock.domain.Stock;
import lombok.*;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@JsonIgnoreProperties(ignoreUnknown = true)
public class StockInfoDto {
    private String srtnCd;
    private String isinCd;
    private String mrktCtg;
    private String itmsNm;
    private String crno;
    private String corpNm;

    @Builder
    public StockInfoDto(String srtnCd, String isinCd, String mrktCtg, String itmsNm, String crno, String corpNm) {
        this.srtnCd = srtnCd;
        this.isinCd = isinCd;
        this.mrktCtg = mrktCtg;
        this.itmsNm = itmsNm;
        this.crno = crno;
        this.corpNm = corpNm;
    }

    public static Stock toEntity(StockInfoDto dto) {
        return Stock.builder()
                .isinCd(dto.getIsinCd())
                .srtnCd(dto.getSrtnCd())
                .itmsNm(dto.getItmsNm())
                .mrktCtg(dto.getMrktCtg())
                .crno(dto.getCrno())
                .enpNm(dto.getCorpNm())
                .build();
    }
}
