package com.stockport.server.domain.indexData.service;

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
class IndexDataServiceImplTest {

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

    @Test
    @DisplayName("updateHistoricalIndexData: 어댑터에서 연도별 기간 데이터를 모아 prevClosePrice 체인으로 저장한다")
    void updateHistoricalIndexData_savesChainedPeriodData() {
        // given: 어댑터가 호출될 때마다 동일한 2건의 기간 데이터 반환(문자열 기반)
        List<KisIndexPeriodPrice> period = new ArrayList<>();
        period.add(KisIndexPeriodPrice.builder()
                .baseDate("20200101")
                .openPrice("100.00")
                .highPrice("120.00")
                .lowPrice("90.00")
                .closePrice("110.00")
                .build());
        period.add(KisIndexPeriodPrice.builder()
                .baseDate("20200102")
                .openPrice("110.00")
                .highPrice("130.00")
                .lowPrice("100.00")
                .closePrice("120.00")
                .build());

        when(kisIndexPriceAdaptor.getIndexPeriodPrice(
                anyString(), any(LocalDate.class), any(LocalDate.class))
        ).thenReturn(period);

        // 모든 날짜가 신규라고 가정 → existsBy... = false
        when(indexDataRepository.existsByMarketTypeAndBaseDate(any(MarketType.class), any(LocalDate.class)))
                .thenReturn(false);

        // save는 그대로 받은 엔티티를 반환
        when(indexDataRepository.save(any(IndexData.class)))
                .thenAnswer(inv -> inv.getArgument(0));

        // when
        indexDataService.updateHistoricalIndexData(MarketType.KOSPI);

        // then
        // 1) 10년 구간(포함) → 연단위 루프 11회, 매회 2건 반환 ⇒ 총 22건 save 시도
        // (정확한 호출수 단정)
        verify(kisIndexPriceAdaptor, times(11))
                .getIndexPeriodPrice(eq(MarketType.KOSPI.getCode()), any(LocalDate.class), any(LocalDate.class));
        verify(indexDataRepository, times(22)).save(any(IndexData.class));

        // 2) 저장된 엔티티들을 캡처하여 prevClosePrice 체인 계산 검증
        ArgumentCaptor<IndexData> savedCaptor = ArgumentCaptor.forClass(IndexData.class);
        verify(indexDataRepository, atLeast(2)).save(savedCaptor.capture());
        List<IndexData> allSaved = savedCaptor.getAllValues();
        assertThat(allSaved).isNotEmpty();

        // 첫 번째 저장: prevClosePrice = 첫 DTO의 open(100.00)
        // changeAmount = 110.00 - 100.00 = 10.00
        // changeRate   = 10.00 * 100 / 100.00 = 10.00
        IndexData first = allSaved.get(0);
        assertThat(first.getBaseDate()).isEqualTo(LocalDate.of(2020, 1, 1));
        assertThat(first.getOpenPrice()).isEqualByComparingTo("100.00");
        assertThat(first.getClosePrice()).isEqualByComparingTo("110.00");
        assertThat(first.getChangeAmount()).isEqualByComparingTo("10.00");
        assertThat(first.getChangeRate()).isEqualByComparingTo("10.00");

        // 두 번째 저장: prevClosePrice = 직전 엔티티의 close(110.00)
        // changeAmount = 120.00 - 110.00 = 10.00
        // changeRate   = 10.00 * 100 / 110.00 = 9.09 (HALF_UP로 소수 둘째 반올림)
        IndexData second = allSaved.get(1);
        assertThat(second.getBaseDate()).isEqualTo(LocalDate.of(2020, 1, 2));
        assertThat(second.getOpenPrice()).isEqualByComparingTo("110.00");
        assertThat(second.getClosePrice()).isEqualByComparingTo("120.00");
        assertThat(second.getChangeAmount()).isEqualByComparingTo("10.00");
        assertThat(second.getChangeRate()).isEqualByComparingTo("9.09");

        // 3) 존재 여부 체크가 저장 전에 매번 수행됨을 최소한으로 검증
        verify(indexDataRepository, atLeast(2))
                .existsByMarketTypeAndBaseDate(eq(MarketType.KOSPI), any(LocalDate.class));
    }

    @Test
    @DisplayName("updateHistoricalIndexData: 이미 존재하는 날짜는 저장하지 않는다")
    void updateHistoricalIndexData_skipsExistingDates() {
        // given: 1건만 반환하고, 그 날짜는 이미 존재한다고 가정
        List<KisIndexPeriodPrice> period = List.of(
                KisIndexPeriodPrice.builder()
                        .baseDate("20200101")
                        .openPrice("100.00")
                        .highPrice("120.00")
                        .lowPrice("90.00")
                        .closePrice("110.00")
                        .build()
        );

        when(kisIndexPriceAdaptor.getIndexPeriodPrice(
                anyString(), any(LocalDate.class), any(LocalDate.class))
        ).thenReturn(period);

        when(indexDataRepository.existsByMarketTypeAndBaseDate(any(MarketType.class), any(LocalDate.class)))
                .thenReturn(true); // 이미 존재 → save 안 됨

        // when
        indexDataService.updateHistoricalIndexData(MarketType.KOSPI);

        // then
        verify(indexDataRepository, never()).save(any(IndexData.class));
    }
}