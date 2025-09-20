package com.stockport.server.stock.client;

import com.stockport.server.stock.dto.StockPriceDto;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class StockPriceApiClientTest {

    @Autowired
    private StockPriceApiClient client;

    @Test
    @DisplayName("기준일자(basDt)로 실제 API 호출")
    void getAllStockPricesByDate_real() {
        StockPriceDto[] result = client.getAllStockPricesByDate("20240919");
        assertNotNull(result);
        assertTrue(result.length > 0);
        for (StockPriceDto dto : result) {
            System.out.printf(
                    "[StockPriceDto] basDt=%s, clpr=%s, vs=%s, fltRt=%s, mkp=%s, hipr=%s, lopr=%s, trqu=%s, trPrc=%s, listShrs=%s, mrktTotAmt=%s, isinCd=%s%n",
                    dto.getBasDt(), dto.getClpr(), dto.getVs(), dto.getFltRt(), dto.getMkp(),
                    dto.getHipr(), dto.getLopr(), dto.getTrqu(), dto.getTrPrc(), dto.getLstgStCnt(),
                    dto.getMrktTotAmt(), dto.getIsinCd()
            );
        }
    }

    @Test
    @DisplayName("종목코드(isinCd)로 실제 API 호출")
    void getStockPriceHistory_real() {
        StockPriceDto[] result = client.getStockPriceHistory("KR7005930003");
        assertNotNull(result);
        assertTrue(result.length > 0);
        for (StockPriceDto dto : result) {
            System.out.printf(
                    "[StockPriceDto] basDt=%s, clpr=%s, vs=%s, fltRt=%s, mkp=%s, hipr=%s, lopr=%s, trqu=%s, trPrc=%s, listShrs=%s, mrktTotAmt=%s, isinCd=%s%n",
                    dto.getBasDt(), dto.getClpr(), dto.getVs(), dto.getFltRt(), dto.getMkp(),
                    dto.getHipr(), dto.getLopr(), dto.getTrqu(), dto.getTrPrc(), dto.getLstgStCnt(),
                    dto.getMrktTotAmt(), dto.getIsinCd()
            );
        }
    }
}