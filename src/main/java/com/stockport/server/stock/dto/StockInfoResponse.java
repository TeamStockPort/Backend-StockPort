package com.stockport.server.stock.dto;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class StockInfoResponse {
    private String basDt;
    private String srtnCd;
    private String isinCd;
    private String mrktCtg;
    private String itmsNm;
    private String crno;
    private String corpNm;

    @Builder
    public StockInfoResponse(String basDt, String srtnCd, String isinCd, String mrktCtg, String itmsNm, String crno, String corpNm) {
        this.basDt = basDt;
        this.srtnCd = srtnCd;
        this.isinCd = isinCd;
        this.mrktCtg = mrktCtg;
        this.itmsNm = itmsNm;
        this.crno = crno;
        this.corpNm = corpNm;
    }
}
