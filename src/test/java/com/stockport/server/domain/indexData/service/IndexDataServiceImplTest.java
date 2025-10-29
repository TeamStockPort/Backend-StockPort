package com.stockport.server.domain.indexData.service;

import com.stockport.server.IntegrationTestSupport;
import com.stockport.server.application.service.indexData.IndexDataServiceImpl;
import com.stockport.server.domain.indexData.constant.MarketType;
import com.stockport.server.domain.indexData.entity.IndexData;
import com.stockport.server.domain.indexData.repository.IndexDataRepository;
import com.stockport.server.global.feign.adaptor.KisIndexPriceAdaptor;
import com.stockport.server.global.feign.dto.KisIndexCurrentPrice;
import com.stockport.server.global.feign.dto.KisIndexPeriodPrice;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import org.springframework.boot.test.context.SpringBootTest;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.mockito.Mockito.*;
import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
class IndexDataServiceImplTest extends IntegrationTestSupport {

    @Mock
    private IndexDataRepository indexDataRepository;

    @Mock
    private KisIndexPriceAdaptor kisIndexPriceAdaptor;

    @InjectMocks
    private IndexDataServiceImpl indexDataService;

    private KisIndexCurrentPrice mockPrice;
    private IndexData existingData;

    @BeforeEach
    void setUp() {
        mockPrice = KisIndexCurrentPrice.builder()
                .currentPrice("2560")
                .highPrice("2600")
                .lowPrice("2500")
                .openPrice("2550")
                .changeSign("-")
                .changeAmount("30")
                .changeRate("1.2")
                .build();

        existingData = IndexData.builder()
                .id(1L)
                .marketType(MarketType.KOSPI)
                .baseDate(LocalDate.now())
                .closePrice(BigDecimal.valueOf(2500))
                .build();
    }

    @Test
    @DisplayName("이미 오늘 날짜의 데이터가 존재할 경우, updateClosePrice()가 호출되어 갱신된다")
    void updateExistingIndexData() {
        // given
        when(kisIndexPriceAdaptor.getIndexCurrentPrice(MarketType.KOSPI.getCode()))
                .thenReturn(mockPrice);
        when(indexDataRepository.findByMarketTypeAndBaseDate(MarketType.KOSPI, LocalDate.now()))
                .thenReturn(Optional.of(existingData));

        // when
        indexDataService.updateCurrentIndexData(MarketType.KOSPI);

        // then
        verify(indexDataRepository, never()).save(any());
        verify(indexDataRepository, times(1)).findByMarketTypeAndBaseDate(any(), any());
        assertThat(existingData.getClosePrice()).isEqualTo(mockPrice.toEntity(MarketType.KOSPI, LocalDate.now()).getClosePrice());
    }

    @Test
    @DisplayName("오늘 날짜의 데이터가 존재하지 않을 경우, 새로운 IndexData가 저장된다")
    void saveNewIndexData() {
        // given
        when(kisIndexPriceAdaptor.getIndexCurrentPrice(MarketType.KOSPI.getCode()))
                .thenReturn(mockPrice);
        when(indexDataRepository.findByMarketTypeAndBaseDate(MarketType.KOSPI, LocalDate.now()))
                .thenReturn(Optional.empty());
        when(indexDataRepository.save(any(IndexData.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        // when
        indexDataService.updateCurrentIndexData(MarketType.KOSPI);

        // then
        verify(indexDataRepository, times(1)).save(any(IndexData.class));
        verify(indexDataRepository, times(1)).findByMarketTypeAndBaseDate(any(), any());
    }
}