package com.stockport.server.global.feign.client;

import com.stockport.server.global.feign.dto.KisMultieStockCurrentPrice;
import com.stockport.server.global.feign.dto.KisStockPeriodPrice;
import com.stockport.server.global.feign.dto.wrapper.KisResponseWrapper;
import com.stockport.server.global.feign.dto.KisStockCurrentPrice;
import com.stockport.server.global.feign.dto.wrapper.KisPeriodResponseWrapper;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@FeignClient(
        name = "kisStockPriceClient",
        url = "https://openapi.koreainvestment.com:9443"
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

    @GetMapping("/uapi/domestic-stock/v1/quotations/intstock-multprice")
    KisResponseWrapper<List<KisMultieStockCurrentPrice>> getMultiStockPrice(
            @RequestHeader("content-type") String contentType,
            @RequestHeader("authorization") String bearerToken,
            @RequestHeader("appkey") String appKey,
            @RequestHeader("appsecret") String appSecret,
            @RequestHeader("tr_id") String trId,
            @RequestHeader("custtype") String custType,
            @RequestParam("FID_COND_MRKT_DIV_CODE_1") String marketCode1,
            @RequestParam("FID_COND_MRKT_DIV_CODE_2") String marketCode2,
            @RequestParam("FID_COND_MRKT_DIV_CODE_3") String marketCode3,
            @RequestParam("FID_COND_MRKT_DIV_CODE_4") String marketCode4,
            @RequestParam("FID_COND_MRKT_DIV_CODE_5") String marketCode5,
            @RequestParam("FID_COND_MRKT_DIV_CODE_6") String marketCode6,
            @RequestParam("FID_COND_MRKT_DIV_CODE_7") String marketCode7,
            @RequestParam("FID_COND_MRKT_DIV_CODE_8") String marketCode8,
            @RequestParam("FID_COND_MRKT_DIV_CODE_9") String marketCode9,
            @RequestParam("FID_COND_MRKT_DIV_CODE_10") String marketCode10,
            @RequestParam("FID_COND_MRKT_DIV_CODE_11") String marketCode11,
            @RequestParam("FID_COND_MRKT_DIV_CODE_12") String marketCode12,
            @RequestParam("FID_COND_MRKT_DIV_CODE_13") String marketCode13,
            @RequestParam("FID_COND_MRKT_DIV_CODE_14") String marketCode14,
            @RequestParam("FID_COND_MRKT_DIV_CODE_15") String marketCode15,
            @RequestParam("FID_COND_MRKT_DIV_CODE_16") String marketCode16,
            @RequestParam("FID_COND_MRKT_DIV_CODE_17") String marketCode17,
            @RequestParam("FID_COND_MRKT_DIV_CODE_18") String marketCode18,
            @RequestParam("FID_COND_MRKT_DIV_CODE_19") String marketCode19,
            @RequestParam("FID_COND_MRKT_DIV_CODE_20") String marketCode20,
            @RequestParam("FID_COND_MRKT_DIV_CODE_21") String marketCode21,
            @RequestParam("FID_COND_MRKT_DIV_CODE_22") String marketCode22,
            @RequestParam("FID_COND_MRKT_DIV_CODE_23") String marketCode23,
            @RequestParam("FID_COND_MRKT_DIV_CODE_24") String marketCode24,
            @RequestParam("FID_COND_MRKT_DIV_CODE_25") String marketCode25,
            @RequestParam("FID_COND_MRKT_DIV_CODE_26") String marketCode26,
            @RequestParam("FID_COND_MRKT_DIV_CODE_27") String marketCode27,
            @RequestParam("FID_COND_MRKT_DIV_CODE_28") String marketCode28,
            @RequestParam("FID_COND_MRKT_DIV_CODE_29") String marketCode29,
            @RequestParam("FID_COND_MRKT_DIV_CODE_30") String marketCode30,
            @RequestParam("FID_INPUT_ISCD_1") String stockCode1,
            @RequestParam("FID_INPUT_ISCD_2") String stockCode2,
            @RequestParam("FID_INPUT_ISCD_3") String stockCode3,
            @RequestParam("FID_INPUT_ISCD_4") String stockCode4,
            @RequestParam("FID_INPUT_ISCD_5") String stockCode5,
            @RequestParam("FID_INPUT_ISCD_6") String stockCode6,
            @RequestParam("FID_INPUT_ISCD_7") String stockCode7,
            @RequestParam("FID_INPUT_ISCD_8") String stockCode8,
            @RequestParam("FID_INPUT_ISCD_9") String stockCode9,
            @RequestParam("FID_INPUT_ISCD_10") String stockCode10,
            @RequestParam("FID_INPUT_ISCD_11") String stockCode11,
            @RequestParam("FID_INPUT_ISCD_12") String stockCode12,
            @RequestParam("FID_INPUT_ISCD_13") String stockCode13,
            @RequestParam("FID_INPUT_ISCD_14") String stockCode14,
            @RequestParam("FID_INPUT_ISCD_15") String stockCode15,
            @RequestParam("FID_INPUT_ISCD_16") String stockCode16,
            @RequestParam("FID_INPUT_ISCD_17") String stockCode17,
            @RequestParam("FID_INPUT_ISCD_18") String stockCode18,
            @RequestParam("FID_INPUT_ISCD_19") String stockCode19,
            @RequestParam("FID_INPUT_ISCD_20") String stockCode20,
            @RequestParam("FID_INPUT_ISCD_21") String stockCode21,
            @RequestParam("FID_INPUT_ISCD_22") String stockCode22,
            @RequestParam("FID_INPUT_ISCD_23") String stockCode23,
            @RequestParam("FID_INPUT_ISCD_24") String stockCode24,
            @RequestParam("FID_INPUT_ISCD_25") String stockCode25,
            @RequestParam("FID_INPUT_ISCD_26") String stockCode26,
            @RequestParam("FID_INPUT_ISCD_27") String stockCode27,
            @RequestParam("FID_INPUT_ISCD_28") String stockCode28,
            @RequestParam("FID_INPUT_ISCD_29") String stockCode29,
            @RequestParam("FID_INPUT_ISCD_30") String stockCode30
    );
}