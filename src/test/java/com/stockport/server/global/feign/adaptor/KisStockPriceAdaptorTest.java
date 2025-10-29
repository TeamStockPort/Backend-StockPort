package com.stockport.server.global.feign.adaptor;

import com.stockport.server.global.apipayload.code.status.ErrorStatus;
import com.stockport.server.global.exception.GeneralException;
import com.stockport.server.global.feign.auth.KisTokenHolder;
import com.stockport.server.global.feign.client.KisStockPriceClient;
import com.stockport.server.global.feign.dto.wrapper.KisResponseWrapper;
import com.stockport.server.global.feign.dto.KisStockCurrentPrice;
import com.stockport.server.global.feign.dto.KisStockPeriodPrice;
import com.stockport.server.global.feign.dto.wrapper.KisPeriodResponseWrapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

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
                "97000", "98200", "98200", "98600", "700", "0.72"
        );

        KisResponseWrapper<KisStockCurrentPrice> wrapper = new KisResponseWrapper<>();
        ReflectionTestUtils.setField(wrapper, "resultCode", "0");
        ReflectionTestUtils.setField(wrapper, "message", "성공");
        ReflectionTestUtils.setField(wrapper, "output", price);

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
        KisResponseWrapper<KisStockCurrentPrice> wrapper = new KisResponseWrapper<>();
        ReflectionTestUtils.setField(wrapper, "resultCode", "1");
        ReflectionTestUtils.setField(wrapper, "message", "에러 발생");

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
    @DisplayName("정상적으로 기간별 주가 정보를 조회하면 KisStockPeriodPriceWrapper를 반환한다")
    void givenValidStockCode_whenGetStockPeriodPrice_thenReturnWrapper() {
        // given
        when(tokenHolder.getAccessToken()).thenReturn("mockToken");
        when(tokenHolder.getAppKey()).thenReturn("mockAppKey");
        when(tokenHolder.getAppSecret()).thenReturn("mockSecret");

        String stockCode = "005930";
        LocalDate startDate = LocalDate.of(2024, 10, 1);
        LocalDate endDate = LocalDate.of(2024, 10, 21);

        var currentPrice = KisStockCurrentPrice.create(
                "97000", "98000", "98500", "96500", "1000", "1.03"
        );

        var periodPrice = KisStockPeriodPrice.builder()
                .baseDate("20241021")
                .openPrice("97000")
                .closePrice("98000")
                .highPrice("98500")
                .lowPrice("96500")
                .changeAmount("1000")
                .changeSign("+")
                .build();

        var wrapper = KisPeriodResponseWrapper.<KisStockCurrentPrice, KisStockPeriodPrice>builder()
                .resultCode("0")
                .message("성공")
                .output1(currentPrice)
                .output2(List.of(periodPrice))
                .build();

        when(kisStockPriceClient.getPeriodPrice(
                anyString(), anyString(), anyString(), anyString(),
                anyString(), anyString(), anyString(),
                eq(stockCode),
                anyString(), anyString(),
                anyString(), anyString()
        )).thenReturn(wrapper);

        // when
        var result = kisStockPriceAdaptor.getStockPeriodPrice(stockCode, startDate, endDate);

        // then
        assertThat(result).isNotNull();
        assertThat(result.getResultCode()).isEqualTo("0");
        assertThat(result.getOutput2()).hasSize(1);
        assertThat(result.getOutput2().get(0).getClosePrice()).isEqualTo("98000");
    }

    @Test
    @DisplayName("기간별 주가 조회 응답 코드가 0이 아니면 GeneralException을 던진다")
    void givenErrorResponse_whenGetStockPeriodPrice_thenThrowException() {
        // given
        String stockCode = "005930";
        LocalDate startDate = LocalDate.of(2024, 10, 1);
        LocalDate endDate = LocalDate.of(2024, 10, 21);

        var wrapper = KisPeriodResponseWrapper.<KisStockCurrentPrice, KisStockPeriodPrice>builder()
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
}