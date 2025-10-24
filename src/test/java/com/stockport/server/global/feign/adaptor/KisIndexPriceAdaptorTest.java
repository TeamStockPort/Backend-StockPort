package com.stockport.server.global.feign.adaptor;

import com.stockport.server.global.apipayload.code.status.ErrorStatus;
import com.stockport.server.global.exception.GeneralException;
import com.stockport.server.global.feign.auth.KisTokenHolder;
import com.stockport.server.global.feign.client.KisIndexClient;
import com.stockport.server.global.feign.dto.KisIndexCurrentPrice;
import com.stockport.server.global.feign.dto.wrapper.KisResponseWrapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class KisIndexPriceAdaptorTest {

    @Mock
    private KisIndexClient kisIndexClient;

    @Mock
    private KisTokenHolder tokenHolder;

    @InjectMocks
    private KisIndexPriceAdaptor kisIndexPriceAdaptor;

    private KisIndexCurrentPrice mockPrice;

    @BeforeEach
    void setUp() {
        mockPrice = KisIndexCurrentPrice.builder()
                .currentPrice("2450.12")
                .highPrice("2470.00")
                .lowPrice("2435.50")
                .changeAmount("12.34")
                .changeRate("0.5")
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
}