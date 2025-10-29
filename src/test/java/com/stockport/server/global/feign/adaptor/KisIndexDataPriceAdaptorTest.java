package com.stockport.server.global.feign.adaptor;

import com.stockport.server.IntegrationTestSupport;
import com.stockport.server.global.apipayload.code.status.ErrorStatus;
import com.stockport.server.global.exception.GeneralException;
import com.stockport.server.global.feign.auth.KisTokenHolder;
import com.stockport.server.global.feign.client.KisIndexClient;
import com.stockport.server.global.feign.dto.KisIndexCurrentPrice;
import com.stockport.server.global.feign.dto.KisIndexPeriodPrice;
import com.stockport.server.global.feign.dto.wrapper.KisPeriodResponseWrapper;
import com.stockport.server.global.feign.dto.wrapper.KisResponseWrapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.List;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class KisIndexDataPriceAdaptorTest {

    @Mock
    private KisIndexClient kisIndexClient;

    @Mock
    private KisTokenHolder tokenHolder;

    @InjectMocks
    private KisIndexPriceAdaptor kisIndexPriceAdaptor;

    private KisIndexCurrentPrice mockPrice;
    private KisIndexPeriodPrice mockPeriodPrice;

    @BeforeEach
    void setUp() {
        mockPrice = KisIndexCurrentPrice.builder()
                .currentPrice("2450.12")
                .highPrice("2470.00")
                .lowPrice("2435.50")
                .changeAmount("12.34")
                .changeRate("0.5")
                .build();
        
        mockPeriodPrice = KisIndexPeriodPrice.builder()
                .baseDate("20251024")
                .openPrice("2450.0")
                .highPrice("2470.0")
                .lowPrice("2435.0")
                .closePrice("2465.0")
                .build();
    }

    @Test
    @DisplayName("정상적으로 인덱스 정보를 조회하면 KisIndexCurrentPrice를 반환한다")
    void givenValidIndexCode_whenGetIndexCurrentPrice_thenReturnPrice() {
        // given
        String indexCode = "0001"; // 코스피
        when(tokenHolder.getAccessToken()).thenReturn("mockToken");
        when(tokenHolder.getAppKey()).thenReturn("mockAppKey");
        when(tokenHolder.getAppSecret()).thenReturn("mockAppSecret");

        KisResponseWrapper<KisIndexCurrentPrice> wrapper =
                KisResponseWrapper.<KisIndexCurrentPrice>builder()
                        .resultCode("0")
                        .message("성공")
                        .output(mockPrice)
                        .build();

        when(kisIndexClient.getIndexPrice(
                anyString(), anyString(), anyString(), anyString(),
                anyString(), anyString(), anyString(), eq(indexCode)
        )).thenReturn(wrapper);

        // when
        KisIndexCurrentPrice result = kisIndexPriceAdaptor.getIndexCurrentPrice(indexCode);

        // then
        assertThat(result).isNotNull();
        assertThat(result.getCurrentPrice()).isEqualTo("2450.12");

        verify(kisIndexClient, times(1)).getIndexPrice(
                anyString(), anyString(), anyString(), anyString(),
                anyString(), anyString(), anyString(), eq(indexCode)
        );
    }

    @Test
    @DisplayName("응답 코드가 0이 아니면 GeneralException을 던진다")
    void givenErrorResponse_whenGetIndexCurrentPrice_thenThrowGeneralException() {
        // given
        String indexCode = "0001";
        KisResponseWrapper<KisIndexCurrentPrice> wrapper =
                KisResponseWrapper.<KisIndexCurrentPrice>builder()
                        .resultCode("1")
                        .message("에러 발생")
                        .build();

        when(kisIndexClient.getIndexPrice(
                anyString(), anyString(), anyString(), anyString(),
                anyString(), anyString(), anyString(), eq(indexCode)
        )).thenReturn(wrapper);

        // when & then
        assertThatThrownBy(() -> kisIndexPriceAdaptor.getIndexCurrentPrice(indexCode))
                .isInstanceOf(GeneralException.class)
                .hasMessageContaining(ErrorStatus.FEIGN_ERROR.getMessage());
    }

    @Test
    @DisplayName("FeignClient 호출 도중 예외 발생 시 GeneralException을 던진다")
    void givenFeignException_whenGetIndexCurrentPrice_thenThrowGeneralException() {
        // given
        String indexCode = "0001";
        when(kisIndexClient.getIndexPrice(
                anyString(), anyString(), anyString(), anyString(),
                anyString(), anyString(), anyString(), eq(indexCode)
        )).thenThrow(new RuntimeException("Feign 통신 오류"));

        // when & then
        assertThatThrownBy(() -> kisIndexPriceAdaptor.getIndexCurrentPrice(indexCode))
                .isInstanceOf(GeneralException.class)
                .hasMessageContaining(ErrorStatus.FEIGN_ERROR.getMessage());
    }

    @Test
    @DisplayName("정상적으로 업종 기간별 시세를 조회하면 KisPeriodResponseWrapper를 반환한다")
    void givenValidIndexCode_whenGetIndexPeriodPrice_thenReturnWrapper() {
        // given
        String indexCode = "0001"; // 코스피
        LocalDate start = LocalDate.of(2025, 10, 1);
        LocalDate end = LocalDate.of(2025, 10, 24);

        when(tokenHolder.getAccessToken()).thenReturn("mockToken");
        when(tokenHolder.getAppKey()).thenReturn("mockAppKey");
        when(tokenHolder.getAppSecret()).thenReturn("mockSecret");

        KisPeriodResponseWrapper<KisIndexCurrentPrice, KisIndexPeriodPrice> wrapper =
                KisPeriodResponseWrapper.<KisIndexCurrentPrice, KisIndexPeriodPrice>builder()
                        .resultCode("0")
                        .message("성공")
                        .output1(mockPrice)
                        .output2(List.of(mockPeriodPrice))
                        .build();

        when(kisIndexClient.getIndexPeriodPrice(
                anyString(),  // content-type
                anyString(),  // token
                anyString(),  // appKey
                anyString(),  // appSecret
                anyString(),  // FHKUP03500100
                anyString(),  // P
                anyString(),  // U
                eq(indexCode), // indexCode (동적으로 비교)
                anyString(),  // startDate
                anyString(),  // endDate
                anyString()   // D
        )).thenReturn(wrapper);

        // when
        var result = kisIndexPriceAdaptor.getIndexPeriodPrice(indexCode, start, end);

        // then
        assertThat(result).isNotNull();

        verify(kisIndexClient, times(1)).getIndexPeriodPrice(
                anyString(),  // content-type
                anyString(),  // token
                anyString(),  // appKey
                anyString(),  // appSecret
                anyString(),  // FHKUP03500100
                anyString(),  // P
                anyString(),  // U
                eq(indexCode), // indexCode (동적으로 비교)
                anyString(),  // startDate
                anyString(),  // endDate
                anyString()   // D
        );
    }

    @Test
    @DisplayName("응답 코드가 0이 아니면 GeneralException을 던진다")
    void givenErrorResponse_whenGetIndexPeriodPrice_thenThrowException() {
        // given
        String indexCode = "0001";
        LocalDate start = LocalDate.of(2025, 10, 1);
        LocalDate end = LocalDate.of(2025, 10, 24);

        KisPeriodResponseWrapper<KisIndexCurrentPrice, KisIndexPeriodPrice> wrapper =
                KisPeriodResponseWrapper.<KisIndexCurrentPrice, KisIndexPeriodPrice>builder()
                        .resultCode("1")
                        .message("에러 발생")
                        .build();

        when(kisIndexClient.getIndexPeriodPrice(
                anyString(), anyString(), anyString(), anyString(),
                anyString(), anyString(), anyString(),
                eq(indexCode),
                anyString(), anyString(),
                anyString()
        )).thenReturn(wrapper);

        // when & then
        assertThatThrownBy(() -> kisIndexPriceAdaptor.getIndexPeriodPrice(indexCode, start, end))
                .isInstanceOf(GeneralException.class)
                .hasMessageContaining(ErrorStatus.FEIGN_ERROR.getMessage());
    }

    @Test
    @DisplayName("FeignClient 호출 중 예외가 발생하면 GeneralException을 던진다")
    void givenFeignException_whenGetIndexPeriodPrice_thenThrowGeneralException() {
        // given
        String indexCode = "0001";
        LocalDate start = LocalDate.of(2025, 10, 1);
        LocalDate end = LocalDate.of(2025, 10, 24);

        when(kisIndexClient.getIndexPeriodPrice(any(), any(), any(), any(), any(), any(), any(), any(), any(), any(), any()))
                .thenThrow(new RuntimeException("Feign 통신 오류"));

        // when & then
        assertThatThrownBy(() -> kisIndexPriceAdaptor.getIndexPeriodPrice(indexCode, start, end))
                .isInstanceOf(GeneralException.class)
                .hasMessageContaining(ErrorStatus.FEIGN_ERROR.getMessage());
    }
}