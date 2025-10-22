package com.stockport.server.global.feign.adaptor;

import com.stockport.server.global.apipayload.code.status.ErrorStatus;
import com.stockport.server.global.exception.GeneralException;
import com.stockport.server.global.feign.auth.KisTokenHolder;
import com.stockport.server.global.feign.client.KisStockPriceClient;
import com.stockport.server.global.feign.dto.KisStockCurrentPrice;
import com.stockport.server.global.feign.dto.KisStockCurrentPriceWrapper;
import com.stockport.server.global.feign.dto.KisStockPeriodPrice;
import com.stockport.server.global.feign.dto.KisStockPeriodPriceWrapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.junit.jupiter.api.extension.ExtendWith;

import java.time.LocalDate;
import java.util.List;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class KisStockPriceAdaptorTest {

    @Mock
    private KisStockPriceClient kisStockPriceClient;

    @Mock
    private KisTokenHolder tokenHolder;

    @InjectMocks
    private KisStockPriceAdaptor kisStockPriceAdaptor;

    @Test
    @DisplayName("정상적으로 주가 정보를 조회하면 KisStockCurrentPrice를 반환한다")
    void givenValidStockCode_whenGetStockPrice_thenReturnCurrentPrice() {
        // given
        String stockCode = "005930";
        when(tokenHolder.getAccessToken()).thenReturn("mockToken");
        when(tokenHolder.getAppKey()).thenReturn("mockAppKey");
        when(tokenHolder.getAppSecret()).thenReturn("mockSecret");

        KisStockCurrentPrice price = KisStockCurrentPrice.create(
                "97500", "98200", "98600", "95500", "700", "0.72"
        );
        KisStockCurrentPriceWrapper wrapper = createKisStockCurrentPriceWrapper("0", price);

        when(kisStockPriceClient.getStockPrice(
                anyString(), anyString(), anyString(), anyString(),
                anyString(), anyString(), anyString(), eq(stockCode)
        )).thenReturn(wrapper);

        // when
        KisStockCurrentPrice result = kisStockPriceAdaptor.getStockCurrentPrice(stockCode);

        // then
        assertThat(result).isNotNull();
        assertThat(result.getCurrentPrice()).isEqualTo("98200");
        verify(kisStockPriceClient, times(1)).getStockPrice(
                anyString(), anyString(), anyString(), anyString(),
                anyString(), anyString(), anyString(), eq(stockCode)
        );
    }

    @Test
    @DisplayName("응답 코드가 0이 아니면 GeneralException 발생")
    void givenErrorResponse_whenGetStockCurrentPrice_thenThrowGeneralException() {
        // given
        String stockCode = "005930";
        when(tokenHolder.getAccessToken()).thenReturn("mockToken");
        when(tokenHolder.getAppKey()).thenReturn("mockAppKey");
        when(tokenHolder.getAppSecret()).thenReturn("mockSecret");

        KisStockCurrentPriceWrapper wrapper = createKisStockCurrentPriceWrapper("1", null);

        when(kisStockPriceClient.getStockPrice(
                anyString(), anyString(), anyString(), anyString(),
                anyString(), anyString(), anyString(), eq(stockCode)
        )).thenReturn(wrapper);

        // when & then
        assertThatThrownBy(() -> kisStockPriceAdaptor.getStockCurrentPrice(stockCode))
                .isInstanceOf(GeneralException.class)
                .hasMessageContaining(ErrorStatus.FEIGN_ERROR.getMessage());
    }

    @Test
    @DisplayName("Feign 호출 중 예외 발생 시 GeneralException 발생")
    void givenFeignFailure_whenGetStockCurrentPrice_thenThrowGeneralException() {
        // given
        String stockCode = "005930";
        when(tokenHolder.getAccessToken()).thenReturn("mockToken");
        when(tokenHolder.getAppKey()).thenReturn("mockAppKey");
        when(tokenHolder.getAppSecret()).thenReturn("mockSecret");

        when(kisStockPriceClient.getStockPrice(
                anyString(), anyString(), anyString(), anyString(),
                anyString(), anyString(), anyString(), eq(stockCode)
        )).thenThrow(new RuntimeException("Feign 호출 실패"));

        // when & then
        assertThatThrownBy(() -> kisStockPriceAdaptor.getStockCurrentPrice(stockCode))
                .isInstanceOf(GeneralException.class)
                .hasMessageContaining(ErrorStatus.FEIGN_ERROR.getMessage());
    }

    @Test
    @DisplayName("정상적으로 기간별 주가 정보를 조회하면 KisStockPeriodPriceWrapper를 반환한다")
    void givenValidStockCode_whenGetStockPeriodPrice_thenReturnWrapper() {
        // given
        String stockCode = "005930";
        LocalDate startDate = LocalDate.of(2024, 10, 1);
        LocalDate endDate = LocalDate.of(2024, 10, 21);

        when(tokenHolder.getAccessToken()).thenReturn("mockToken");
        when(tokenHolder.getAppKey()).thenReturn("mockAppKey");
        when(tokenHolder.getAppSecret()).thenReturn("mockSecret");

        KisStockPeriodPrice samplePrice = KisStockPeriodPrice.builder()
                .baseDate("20241021")
                .openPrice("97000")
                .closePrice("98000")
                .highPrice("98500")
                .lowPrice("96500")
                .changeAmount("1000")
                .changeSign("+")
                .build();

        KisStockPeriodPriceWrapper wrapper = KisStockPeriodPriceWrapper.builder()
                .resultCode("0")
                .message("정상처리")
                .stockPeriodPriceList(List.of(samplePrice))
                .build();

        when(kisStockPriceClient.getPeriodPrice(
                anyString(), anyString(), anyString(), anyString(),
                anyString(), anyString(), anyString(),
                eq(stockCode),
                anyString(), anyString(),
                anyString(), anyString()
        )).thenReturn(wrapper);

        // when
        KisStockPeriodPriceWrapper result =
                kisStockPriceAdaptor.getStockPeriodPrice(stockCode, startDate, endDate);

        // then
        assertThat(result).isNotNull();
        assertThat(result.getResultCode()).isEqualTo("0");
        assertThat(result.getStockPeriodPriceList()).hasSize(1);
        assertThat(result.getStockPeriodPriceList().get(0).getClosePrice()).isEqualTo("98000");

        verify(kisStockPriceClient, times(1)).getPeriodPrice(
                anyString(), anyString(), anyString(), anyString(),
                anyString(), anyString(), anyString(),
                eq(stockCode),
                eq("20241001"), eq("20241021"),
                anyString(), anyString()
        );
    }

    @Test
    @DisplayName("응답 코드가 0이 아니면 GeneralException을 던진다")
    void givenErrorResponse_whenGetStockPeriodPrice_thenThrowException() {
        // given
        String stockCode = "005930";
        LocalDate startDate = LocalDate.of(2024, 10, 1);
        LocalDate endDate = LocalDate.of(2024, 10, 21);

        when(tokenHolder.getAccessToken()).thenReturn("mockToken");
        when(tokenHolder.getAppKey()).thenReturn("mockAppKey");
        when(tokenHolder.getAppSecret()).thenReturn("mockSecret");

        KisStockPeriodPriceWrapper wrapper = KisStockPeriodPriceWrapper.builder()
                .resultCode("1")
                .message("에러 발생")
                .build();

        when(kisStockPriceClient.getPeriodPrice(
                anyString(), anyString(), anyString(), anyString(),
                anyString(), anyString(), anyString(),
                eq(stockCode),
                anyString(), anyString(),
                anyString(), anyString()
        )).thenReturn(wrapper);

        // when & then
        assertThatThrownBy(() -> kisStockPriceAdaptor.getStockPeriodPrice(stockCode, startDate, endDate))
                .isInstanceOf(GeneralException.class)
                .hasMessageContaining(ErrorStatus.FEIGN_ERROR.getMessage());
    }

    @Test
    @DisplayName("Feign 호출 중 예외 발생 시 GeneralException 발생")
    void givenFeignFailure_whenGetStockPeriodPrice_thenThrowGeneralException() {
        // given
        String stockCode = "005930";
        LocalDate startDate = LocalDate.of(2024, 10, 1);
        LocalDate endDate = LocalDate.of(2024, 10, 21);

        when(tokenHolder.getAccessToken()).thenReturn("mockToken");
        when(tokenHolder.getAppKey()).thenReturn("mockAppKey");
        when(tokenHolder.getAppSecret()).thenReturn("mockSecret");

        when(kisStockPriceClient.getPeriodPrice(
                anyString(), anyString(), anyString(), anyString(),
                anyString(), anyString(), anyString(),
                eq(stockCode),
                anyString(), anyString(),
                anyString(), anyString()
        )).thenThrow(new RuntimeException("Feign 호출 실패"));

        // when & then
        assertThatThrownBy(() -> kisStockPriceAdaptor.getStockPeriodPrice(stockCode, startDate, endDate))
                .isInstanceOf(GeneralException.class)
                .hasMessageContaining(ErrorStatus.FEIGN_ERROR.getMessage());
    }

    private KisStockCurrentPriceWrapper createKisStockCurrentPriceWrapper(String resultCode, KisStockCurrentPrice stockCurrentPrice) {
        return KisStockCurrentPriceWrapper.builder()
                .resultCode(resultCode)
                .stockCurrentPrice(stockCurrentPrice)
                .build();
    }
}