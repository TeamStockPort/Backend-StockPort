package com.stockport.server.global.feign.client;

import com.stockport.server.global.feign.dto.KisStockPeriodPrice;
import com.stockport.server.global.feign.dto.wrapper.KisResponseWrapper;
import com.stockport.server.global.feign.dto.KisStockCurrentPrice;
import com.stockport.server.global.feign.dto.wrapper.KisPeriodResponseWrapper;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(
        name = "kisStockPriceClient",
        url = "https://openapivts.koreainvestment.com:29443"
)
public interface KisStockPriceClient {

    @GetMapping("/uapi/domestic-stock/v1/quotations/inquire-price")
    KisResponseWrapper<KisStockCurrentPrice> getStockPrice( // 단일 종목 현재 시세
                                                            @RequestHeader("content-type") String contentType,
                                                            @RequestHeader("authorization") String bearerToken,
                                                            @RequestHeader("appkey") String appKey,
                                                            @RequestHeader("appsecret") String appSecret,
                                                            @RequestHeader("tr_id") String trId,                        // API 코드
                                                            @RequestHeader("custtype") String custType,                 // 고객 구분 (P: 개인, B: 법인)

                                                            @RequestParam("FID_COND_MRKT_DIV_CODE") String marketCode,  // 마켓 구분 코드 (J: KRX, NX: NXT, UN: 통합)
                                                            @RequestParam("FID_INPUT_ISCD") String stockCode            // 종목 코드
    );

    @GetMapping("/uapi/domestic-stock/v1/quotations/inquire-daily-itemchartprice")
    KisPeriodResponseWrapper<KisStockCurrentPrice, KisStockPeriodPrice> getPeriodPrice( // 단일 종목 기간별 시세
                                                                  @RequestHeader("content-type") String contentType,
                                                                  @RequestHeader("authorization") String bearerToken,
                                                                  @RequestHeader("appkey") String appKey,
                                                                  @RequestHeader("appsecret") String appSecret,
                                                                  @RequestHeader("tr_id") String trId,                        // API 코드 (FHKST03010100)
                                                                  @RequestHeader("custtype") String custType,                 // 고객 구분 (P: 개인, B: 법인)

                                                                  @RequestParam("FID_COND_MRKT_DIV_CODE") String marketCode,  // 마켓 구분 코드 (J: KRX, NX: NXT, UN: 통합)
                                                                  @RequestParam("FID_INPUT_ISCD") String stockCode,           // 종목 코드
                                                                  @RequestParam("FID_INPUT_DATE_1") String startDate,         // 조회 시작일 (YYYYMMDD)
                                                                  @RequestParam("FID_INPUT_DATE_2") String endDate,           // 조회 종료일 (YYYYMMDD)
                                                                  @RequestParam("FID_PERIOD_DIV_CODE") String periodDiv,      // 시세 단위 (D: 일, W: 주, M: 월, T: 틱)
                                                                  @RequestParam("FID_ORG_ADJ_PRC") String adjusted            // 수정주가 여부 (1: 수정주가, 0: 원시가)

    );
}