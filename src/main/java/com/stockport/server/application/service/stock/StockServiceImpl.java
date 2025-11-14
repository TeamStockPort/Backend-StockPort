package com.stockport.server.application.service.stock;

import com.stockport.server.application.controller.stock.dto.StockInfoResponse;
import com.stockport.server.application.controller.stock.dto.StockPriceResponse;
import com.stockport.server.application.controller.stock.dto.StockQueryResponse;
import com.stockport.server.application.controller.stock.dto.StockRankResponse;
import com.stockport.server.domain.stock.entity.Stock;
import com.stockport.server.domain.stock.entity.StockCurrentPrice;
import com.stockport.server.domain.stock.entity.StockPrice;
import com.stockport.server.domain.stock.repository.StockPriceRepository;
import com.stockport.server.domain.stock.repository.StockRepository;
import com.stockport.server.global.apipayload.code.status.ErrorStatus;
import com.stockport.server.global.exception.GeneralException;
import com.stockport.server.global.feign.adaptor.KisStockPriceAdaptor;
import com.stockport.server.global.feign.dto.KisMultieStockCurrentPrice;
import com.stockport.server.global.feign.dto.KisStockCurrentPrice;
import com.stockport.server.global.feign.dto.KisStockPeriodPrice;
import com.stockport.server.global.feign.dto.wrapper.KisResponseWrapper;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

@Service
@Slf4j
@RequiredArgsConstructor
public class StockServiceImpl implements StockService {
    private final StockRepository stockRepository;
    private final StockPriceRepository stockPriceRepository;
    private final KisStockPriceAdaptor kisStockPriceAdaptor;

    @PersistenceContext
    private EntityManager entityManager;

    @Override
    public Page<StockRankResponse> getStocksByMarketCap(Pageable pageable) {
        Page<Stock> stockPage = stockRepository.findAllByOrderByMarketCapDesc(pageable);

        AtomicInteger index = new AtomicInteger((int) pageable.getOffset() + 1);

        return stockPage.map(stock ->
                StockRankResponse.of(
                        stock,
                        index.getAndIncrement(),
                        List.of(StockPriceResponse.of(stock.getCurrentPriceInfo()))
                )
        );
    }

    @Override
    public StockInfoResponse getStockInfo(String stockCode, LocalDate startDate, LocalDate endDate) {
        Stock stock = stockRepository.findByStockCd(stockCode)
                .orElseThrow(() -> new GeneralException(ErrorStatus.STOCK_NOT_FOUND));
        List<StockPriceResponse> stockPriceResponseList = stockPriceRepository.findByStockAndBaseDateBetween(stock, startDate, endDate)
                .stream().map(StockPriceResponse::of).toList();
        return StockInfoResponse.of(stock, stockPriceResponseList);
    }

    @Override
    public List<StockQueryResponse> searchStocks(String query) {
        List<Stock> stocks = stockRepository.findTop10ByStockNameContainingIgnoreCaseOrStockCdContainingIgnoreCaseOrIsinCdContainingIgnoreCaseOrderByStockNameAsc(query, query, query);

        return stocks.stream()
                .map(StockQueryResponse::of)
                .toList();
    }

    @Override
    @Transactional
    public void updateCurrentStockData() {
        List<Stock> stocks = stockRepository.findAll();
        for (int stockIdx = 0; stockIdx < stocks.size() / 30; stockIdx++) {
            List<Stock> stockList = stocks.subList(stockIdx * 30, Math.min((stockIdx + 1) * 30, stocks.size()));
            List<String> stockCdList = stockList.stream()
                    .map(Stock::getStockCd)
                    .toList();
            List<StockCurrentPrice> stockCurrentPriceList = kisStockPriceAdaptor.getMultiStockCurrentPrice(stockCdList).getOutput().stream()
                    .map(currentPrice -> currentPrice.toEntity(LocalDate.now()))
                    .toList();

            for (int i = 0; i < Math.min(30, stockList.size()); i++)
                stockList.get(i).updateCurrentPriceInfo(stockCurrentPriceList.get(i));
        }
        log.info("[stock] 현재 주가 데이터 업데이트 완료");
    }

    @Override
    @Transactional
    public void updatePeriodicStockData(LocalDate startDate, LocalDate endDate) {
        List<Stock> stocks = stockRepository.findAll();
        for (Stock stock : stocks) {
            List<StockPrice> stockPriceList = kisStockPriceAdaptor.getStockPeriodPrice(stock.getStockCd(), startDate, endDate)
                    .getOutput2().stream()
                    .map(KisStockPeriodPrice::toEntity)
                    .toList();

            for (StockPrice stockPrice : stockPriceList) {
                if (stockPriceRepository.existsByStockAndBaseDate(stock, stockPrice.getBaseDate()))
                    continue;
                stockPrice.updateStock(stock);
                stockPriceRepository.save(stockPrice);
            }
            log.info("[stock] 기간 주가 데이터 업데이트 완료: {} 진행률 {}%", stock.getStockCd(), (stocks.indexOf(stock) + 1) * 100 / stocks.size());
            entityManager.flush();
            entityManager.clear();
        }
    }

    @Override
    @Transactional
    public void updateHistoricalStockData() {
        List<Stock> stocks = stockRepository.findAll();
        LocalDate endDate = LocalDate.now();
        LocalDate startDate = endDate.minusYears(10);

        for (Stock stock : stocks) {
            for (LocalDate updateDate = startDate; updateDate.isBefore(endDate); updateDate = updateDate.plusDays(140)) {
                List<StockPrice> stockPriceList = kisStockPriceAdaptor.getStockPeriodPrice(stock.getStockCd(), updateDate, updateDate.plusDays(139))
                        .getOutput2().stream()
                        .map(KisStockPeriodPrice::toEntity)
                        .toList();
                for (StockPrice stockPrice : stockPriceList) {
                    if (stockPriceRepository.existsByStockAndBaseDate(stock, stockPrice.getBaseDate()))
                        continue;
                    stockPrice.updateStock(stock);
                    stockPriceRepository.save(stockPrice);
                }
            }
            log.info("[stock] 과거 주가 데이터 업데이트 완료: {} 진행률 {}%", stock.getStockCd(), (stocks.indexOf(stock) + 1) * 100 / stocks.size());
            entityManager.flush();
            entityManager.clear();
        }
    }

    @Override
    @Transactional
    public void saveDailyStockData() {
        List<Stock> stockList = stockRepository.findAll();
        for (Stock stock : stockList) {
            if (stockPriceRepository.existsByStockAndBaseDate(stock, LocalDate.now()))
                continue;
            StockPrice stockPrice = stock.getCurrentPriceInfo().toStockPriceEntity();
            stockPrice.updateStock(stock);
            stockPriceRepository.save(stockPrice);
        }
    }
}
