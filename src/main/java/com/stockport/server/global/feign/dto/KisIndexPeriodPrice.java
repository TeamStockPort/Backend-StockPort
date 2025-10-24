package com.stockport.server.global.feign.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
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

    private Integer caculateChangeAmount(String sign, String amount) {
        Integer amt = KisParsingUtils.parseIntSafe(amount);
        if (sign.equals("-")) {
            return -amt;
        }
        return amt;
    }

    private BigDecimal caculateChangeRate(String sign, String changeRate) {
        BigDecimal amt = KisParsingUtils.parseDoubleSafe(changeRate);
        if (sign.equals("-")) {
            return amt.negate();
        }
        return amt;
    }
}
