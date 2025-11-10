package com.stockport.server.application.service.indexData;

import com.stockport.server.application.controller.IndexData.dto.IndexDataResponse;
import com.stockport.server.domain.indexData.constant.MarketType;
import com.stockport.server.domain.indexData.entity.IndexData;
import com.stockport.server.domain.indexData.repository.IndexDataRepository;
import com.stockport.server.global.apipayload.code.status.ErrorStatus;
import com.stockport.server.global.exception.GeneralException;
import com.stockport.server.global.feign.adaptor.KisIndexPriceAdaptor;
import com.stockport.server.global.feign.dto.KisIndexCurrentPrice;
import com.stockport.server.global.feign.dto.KisIndexPeriodPrice;
import com.stockport.server.global.utils.KisParsingUtils;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.swing.text.html.parser.Entity;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class IndexDataServiceImpl implements IndexDataService {
    private final IndexDataRepository indexDataRepository;
    private final KisIndexPriceAdaptor kisIndexPriceAdaptor;
    @PersistenceContext private final EntityManager entityManager;

    @Override
    public void updateCurrentIndexData(MarketType marketType) {
        KisIndexCurrentPrice indexCurrentPrice = kisIndexPriceAdaptor.getIndexCurrentPrice(marketType.getCode());
        indexDataRepository.findByMarketTypeAndBaseDate(marketType, LocalDate.now())
                .ifPresentOrElse(
                        existing -> existing.updateClosePrice(KisParsingUtils.parseBigDecimalSafe(indexCurrentPrice.getCurrentPrice())),
                        () -> indexDataRepository.save(indexCurrentPrice.toEntity(marketType, LocalDate.now()))
                );
    }

    @Override
    public void updateHistoricalIndexData(MarketType marketType) {
        LocalDate endDate = LocalDate.now();
        LocalDate startDate = endDate.minusYears(10);

        List<KisIndexPeriodPrice> indexPeriodPriceList = new ArrayList<>();

        for (LocalDate date = startDate; !date.isAfter(endDate); date = date.plusDays(70)) {
            List<KisIndexPeriodPrice> indexPeriodPrice = kisIndexPriceAdaptor.getIndexPeriodPrice(
                    marketType.getCode(),
                    date,
                    date.plusDays(70).minusDays(1));

            for (int index = indexPeriodPrice.size() - 1; index >= 0; index--)
                indexPeriodPriceList.add(indexPeriodPrice.get(index));

            try {
                 Thread.sleep(600);
             } catch (InterruptedException e) {
                 log.error("[KIS] 조회 대기 중 오류 발생");
             }
        }

        BigDecimal prevClosePrice = KisParsingUtils.parseBigDecimalSafe(indexPeriodPriceList.get(0).getOpenPrice());
        for (KisIndexPeriodPrice kisIndexPeriodPrice : indexPeriodPriceList) {
            IndexData indexData = kisIndexPeriodPrice.toEntity(prevClosePrice, marketType);
            prevClosePrice = indexData.getClosePrice();

            if (!indexDataRepository.existsByMarketTypeAndBaseDate(marketType, indexData.getBaseDate())) {
                indexDataRepository.save(indexData);
            }
        }
    }

    @Override
    public IndexDataResponse getCurrentIndexData(MarketType marketType) {
        IndexData indexData = indexDataRepository.findTopByMarketTypeOrderByBaseDateDesc(marketType)
                .orElseThrow(() -> new GeneralException(ErrorStatus.INDEX_DATA_NOT_FOUND));

        return IndexDataResponse.of(indexData);
    }

    @Override
    public List<IndexDataResponse> getPeriodIndexData(MarketType marketType, LocalDate startDate, LocalDate endDate) {
        List<IndexData> indexDataList = indexDataRepository.findAllByMarketTypeAndBaseDateBetweenOrderByBaseDateAsc(marketType, startDate, endDate);

        return indexDataList.stream()
                .map(IndexDataResponse::of)
                .toList();
    }
}
