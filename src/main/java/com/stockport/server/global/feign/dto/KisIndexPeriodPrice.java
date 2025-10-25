package com.stockport.server.global.feign.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.stockport.server.domain.index.entity.IndexData;
import com.stockport.server.global.apipayload.code.status.ErrorStatus;
import com.stockport.server.global.exception.GeneralException;
import com.stockport.server.global.utils.KisParsingUtils;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class KisIndexPeriodPrice {

    @JsonProperty("stck_bsop_date")
    private String baseDate;        // 기준일 (YYYYMMDD)

    @JsonProperty("bstp_nmix_prpr")
    private String closePrice;      // 종가

    @JsonProperty("bstp_nmix_oprc")
    private String openPrice;       // 시가

    @JsonProperty("bstp_nmix_hgpr")
    private String highPrice;       // 고가

    @JsonProperty("bstp_nmix_lwpr")
    private String lowPrice;        // 저가

    @JsonProperty("prdy_vrss_sign")
    private String changeSign;      // 등락부호

    @JsonProperty("bstp_nmix_prdy_vrss")
    private String changeAmount;    // 등락폭

    @JsonProperty("bstp_nmix_prdy_ctrt")
    private String changeRate;      // 등락률 (%)

    private BigDecimal caculateSign(String sign, String decimal) {
        BigDecimal bigDecimal = KisParsingUtils.parseDoubleSafe(decimal);
        if (sign.equals("-")) {
            return bigDecimal.negate();
        }
        return bigDecimal;
    }

    public IndexData toEntity() {
        return IndexData.builder()
                .baseDate(KisParsingUtils.parseDateSafe(this.baseDate))
                .closePrice(KisParsingUtils.parseDoubleSafe(this.closePrice))
                .openPrice(KisParsingUtils.parseDoubleSafe(this.openPrice))
                .highPrice(KisParsingUtils.parseDoubleSafe(this.highPrice))
                .lowPrice(KisParsingUtils.parseDoubleSafe(this.lowPrice))
                .changeAmount(caculateSign(this.changeSign, this.changeAmount))
                .changeRate(caculateSign(this.changeSign, this.changeRate))
                .build();
    }
}
