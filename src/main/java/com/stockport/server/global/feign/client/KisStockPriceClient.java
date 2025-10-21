package com.stockport.server.global.feign.client;

import com.stockport.server.global.feign.dto.KisStockPriceResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestParam;

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
            @RequestParam("fid_cond_mrkt_div_code") String marketCode,  // 시장 구분 코드 (J: 주식, Q: ETF, E: ELW)
            @RequestParam("fid_input_iscd") String stockCode            // 종목 코드
    );
}