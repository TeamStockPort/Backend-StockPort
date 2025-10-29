package com.stockport.server.global.feign.client;

import com.stockport.server.global.feign.dto.KisIndexCurrentPrice;
import com.stockport.server.global.feign.dto.KisIndexPeriodPrice;
import com.stockport.server.global.feign.dto.wrapper.KisPeriodResponseWrapper;
import com.stockport.server.global.feign.dto.wrapper.KisResponseWrapper;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(
        name = "kisIndexClient",
        url = "https://openapivts.koreainvestment.com:29443"
)
public interface KisIndexClient {
        @GetMapping("/uapi/domestic-stock/v1/quotations/inquire-index-price")
        KisResponseWrapper<KisIndexCurrentPrice> getIndexPrice(
                @RequestHeader("content-type") String contentType,
                @RequestHeader("authorization") String bearerToken,
                @RequestHeader("appkey") String appKey,
                @RequestHeader("appsecret") String appSecret,
                @RequestHeader("tr_id") String trId,                        // 인덱스 조회용 TR ID (FHPUP02100000)
                @RequestHeader("custtype") String custType,

                @RequestParam("FID_COND_MRKT_DIV_CODE") String marketCode,  // 고정값: "U"
                @RequestParam("FID_INPUT_ISCD") String indexCode            // 예: "0001" (코스피), "1001" (코스닥)
        );

        @GetMapping("/uapi/domestic-stock/v1/quotations/inquire-daily-indexchartprice")
        KisPeriodResponseWrapper<KisIndexCurrentPrice, KisIndexPeriodPrice> getIndexPeriodPrice(
                @RequestHeader("content-type") String contentType,
                @RequestHeader("authorization") String bearerToken,
                @RequestHeader("appkey") String appKey,
                @RequestHeader("appsecret") String appSecret,
                @RequestHeader("tr_id") String trId,          // FHKUP03500100 (업종 기간별 시세 조회용)
                @RequestHeader("custtype") String custType,  // P: 개인 / B: 법인

                @RequestParam("FID_COND_MRKT_DIV_CODE") String marketCode,   // U (업종)
                @RequestParam("FID_INPUT_ISCD") String indexCode,            // 업종 코드 (예: 0001: 코스피, 1001: 코스닥)
                @RequestParam("FID_INPUT_DATE_1") String startDate,          // 조회 시작일 (yyyyMMdd)
                @RequestParam("FID_INPUT_DATE_2") String endDate,            // 조회 종료일 (yyyyMMdd)
                @RequestParam("FID_PERIOD_DIV_CODE") String periodCode       // D: 일 / W: 주 / M: 월 / Y: 년
        );
}
