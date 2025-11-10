package com.stockport.server.global.feign.client;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.stockport.server.global.feign.auth.KisTokenHolder;
import com.stockport.server.global.feign.dto.KisStockCurrentPrice;
import com.stockport.server.global.feign.dto.KisStockPeriodPrice;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@Slf4j
@SpringBootTest
class KisStockPriceClientTest {

    @Autowired
    private KisStockPriceClient kisStockPriceClient;

    @Autowired
    private KisTokenHolder tokenHolder;

    @Autowired
    private ObjectMapper objectMapper;

//    @Test
//    @DisplayName("getStockPrice: 실제 API 호출 → Wrapper + 내부 DTO 구조 전체 검증 및 JSON 출력")
//    void getStockPriceTest() throws Exception {
//        // given
//        String contentType = "application/json; charset=utf-8";
//        String bearerToken = "Bearer " + tokenHolder.getAccessToken();
//        String appKey = tokenHolder.getAppKey();
//        String appSecret = tokenHolder.getAppSecret();
//        String trId = "FHKST01010100"; // 국내주식 현재가 조회
//        String custType = "P";         // 개인
//        String marketCode = "UN";       // KRX (유가증권시장)
//        String stockCode = "005930";   // 삼성전자
//
//        log.info("[Request] KIS API 호출 시작");
//        log.info("Headers => tr_id={}, custType={}, marketCode={}, stockCode={}",
//                trId, custType, marketCode, stockCode);
//
//        // when
//        var response = kisStockPriceClient.getStockPrice(
//                contentType, bearerToken, appKey, appSecret, trId, custType, marketCode, stockCode
//        );
//
//        // then
//        assertThat(response).isNotNull();
//        assertThat(response.getResultCode()).isEqualTo("0");
//        assertThat(response.getOutput()).isNotNull();
//
//        // 전체 JSON 로그 출력
//        String json = objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(response);
//        log.info("[Full JSON Response]\n{}", json);
//
//        // 내부 주식 데이터 로깅
//        KisStockCurrentPrice price = response.getOutput();
//        log.info("[Parsed StockCurrentPrice]");
//        log.info("시가: {}", price.getOpenPrice());
//        log.info("현재가: {}", price.getCurrentPrice());
//        log.info("고가: {}", price.getHighPrice());
//        log.info("저가: {}", price.getLowPrice());
//        log.info("등락폭: {}", price.getChangeAmount());
//        log.info("등락률: {}", price.getChangeRate());
//
//        // 검증
//        assertThat(price.getCurrentPrice()).isNotBlank();
//        assertThat(price.getOpenPrice()).isNotBlank();
//        assertThat(price.getHighPrice()).isNotBlank();
//        assertThat(price.getLowPrice()).isNotBlank();
//    }
//
//    @Test
//    @DisplayName("getPeriodPrice: 실제 API 호출 → Wrapper + 내부 DTO 전체 로그 출력")
//    void getStockPeriodPriceTest() throws Exception {
//        // given
//        String contentType = "application/json; charset=utf-8";
//        String bearerToken = "Bearer " + tokenHolder.getAccessToken();
//        String appKey = tokenHolder.getAppKey();
//        String appSecret = tokenHolder.getAppSecret();
//        String trId = "FHKST03010100";  // 국내주식 기간별 시세조회 (일봉)
//        String custType = "P";          // 개인
//        String marketCode = "J";        // KRX (유가증권)
//        String stockCode = "005930";    // 삼성전자
//        String periodCode = "D";        // 일봉
//        String adjustedYn = "1";        // 수정주가 반영
//        String startDate = "20241001";  // 조회 시작일
//        String endDate = "20241021";    // 조회 종료일
//
//        log.info("[Request] KIS 기간별 시세 조회 시작");
//        log.info("Headers => tr_id={}, custType={}, marketCode={}, stockCode={}, periodCode={}, start={}, end={}",
//                trId, custType, marketCode, stockCode, periodCode, startDate, endDate);
//
//        // when
//        var response = kisStockPriceClient.getPeriodPrice(
//                contentType, bearerToken, appKey, appSecret, trId, custType,
//                marketCode, stockCode, periodCode, adjustedYn, startDate, endDate
//        );
//
//        // then
//        assertThat(response).isNotNull();
//        assertThat(response.getResultCode()).isEqualTo("0");
//
//        // 전체 JSON 구조 출력
//        String json = objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(response);
//        log.info("[Full JSON Response]\n{}", json);
//
//        // 내부 시세 데이터 확인
//        List<KisStockPeriodPrice> prices = response.getOutput2();
//        assertThat(prices).isNotEmpty();
//
//        log.info("[Parsed Stock Period Prices]");
//        for (KisStockPeriodPrice p : prices) {
//            log.info("기준일: {} / 시가: {} / 종가: {} / 고가: {} / 저가: {} / 등락폭: {} / 등락부호: {}",
//                    p.getBaseDate(), p.getOpenPrice(), p.getClosePrice(),
//                    p.getHighPrice(), p.getLowPrice(), p.getChangeAmount(), p.getChangeSign());
//        }
//
//        // 첫 번째 데이터 검증 (샘플)
//        KisStockPeriodPrice first = prices.get(0);
//        assertThat(first.getBaseDate()).isNotBlank();
//        assertThat(first.getOpenPrice()).isNotBlank();
//        assertThat(first.getClosePrice()).isNotBlank();
//    }
}