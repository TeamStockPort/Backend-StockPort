package com.stockport.server.application.service.stock;

import com.stockport.server.domain.stock.entity.Stock;
import com.stockport.server.domain.stock.entity.StockPrice;
import com.stockport.server.domain.stock.repository.StockPriceRepository;
import com.stockport.server.global.feign.adaptor.KisStockPriceAdaptor;
import com.stockport.server.global.feign.dto.KisStockPeriodPrice;
import com.stockport.server.global.utils.KisParsingUtils;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Service
@Slf4j
@RequiredArgsConstructor
public class PeriodicStockSaver {

    private final StockPriceRepository stockPriceRepository;
    private final KisStockPriceAdaptor kisStockPriceAdaptor;
    @PersistenceContext private final EntityManager entityManager;

    @Transactional
    public void saveOnePeriod(Stock stock, LocalDate start, LocalDate end) {

        // 1) 해당 종목의 기존 날짜 목록을 한 번에 가져오기
        List<LocalDate> existingDates = stockPriceRepository
                .findAllBaseDatesByStockAndDateRange(stock, start, end);

        Set<LocalDate> existingDateSet = new HashSet<>(existingDates); // O(1) 조회용

        // 2) API 호출하여 새 데이터 변환
        List<StockPrice> newList = kisStockPriceAdaptor.getStockPeriodPrice(stock.getStockCd(), start, end)
                .getOutput2().stream()
                .filter(dto -> !existingDateSet.contains(KisParsingUtils.parseDateSafe(dto.getBaseDate()))) // 필터링
                .map(KisStockPeriodPrice::toEntity)
                .peek(entity -> entity.updateStock(stock))
                .toList();

        // 3) 새 데이터만 saveAll 수행
        if (!newList.isEmpty()) {
            stockPriceRepository.saveAll(newList);
        }

        entityManager.flush();
        entityManager.clear();
    }
}