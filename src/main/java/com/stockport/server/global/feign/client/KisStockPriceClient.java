package com.stockport.server.global.feign.client;

import com.stockport.server.global.feign.dto.KisStockPriceResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@FeignClient(
        name = "kisQuoteClient",
        url = "https://openapivts.koreainvestment.com:29443" // 실계좌는 openapi.koreainvestment.com
)
public interface KisStockPriceClient {

    @GetMapping("/uapi/domestic-stock/v1/quotations/inquire-price")
    KisStockPriceResponse getStockPrice( // 단일 종목 현재 시세
            @RequestHeader("authorization") String bearerToken,
            @RequestHeader("appkey") String appKey,
            @RequestHeader("appsecret") String appSecret,
            @RequestHeader("tr_id") String trId,                        // API 코드
            @RequestHeader("custtype") String custType,                 // 고객 구분 (P: 개인, B: 법인)

            @RequestParam("fid_cond_mrkt_div_code") String marketCode,  // 종목 구분 코드 (J: 주식, Q: ETF, E: ELW)
            @RequestParam("fid_input_iscd") String stockCode            // 종목 코드
    );

    @GetMapping("/uapi/domestic-stock/v1/quotations/inquire-time-itemchartprice")
    List<KisStockPriceResponse> getPeriodPrice(
            @RequestHeader("authorization") String bearerToken,
            @RequestHeader("appkey") String appKey,
            @RequestHeader("appsecret") String appSecret,
            @RequestHeader("tr_id") String trId,                        // API 코드
            @RequestHeader("custtype") String custType,                 // 고객 구분 (P: 개인, B: 법인)

            @RequestParam("fid_cond_mrkt_div_code") String marketCode,  // 종목 구분 코드 (J: 주식, Q: ETF, E: ELW)
            @RequestParam("fid_input_iscd") String stockCode,           // 종목 코드
            @RequestParam("fid_period_div_code") String periodCode,     // 시세 단위 (D: 일, W: 주, M: 월, T: 틱)
            @RequestParam("fid_org_adj_prc") String adjustedYn,         // 수정주가 여부 (1: 수정주가, 0: 원시가)
            @RequestParam("start_time") String startDate,               // 시작일 YYYYMMDD
            @RequestParam("end_time") String endDate                    // 종료일 YYYYMMDD (최대 1년 단위)
    );
}