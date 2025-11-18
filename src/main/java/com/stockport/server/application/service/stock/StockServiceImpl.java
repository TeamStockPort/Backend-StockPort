package com.stockport.server.application.service.stock;

import com.stockport.server.application.controller.stock.dto.StockInfoResponse;
import com.stockport.server.application.controller.stock.dto.StockPriceResponse;
import com.stockport.server.application.controller.stock.dto.StockQueryResponse;
import com.stockport.server.application.controller.stock.dto.StockRankResponse;
import com.stockport.server.domain.stock.entity.Stock;
import com.stockport.server.domain.stock.entity.StockCurrentPrice;
import com.stockport.server.domain.stock.entity.StockPrice;
import com.stockport.server.domain.stock.repository.StockCurrentPriceRepository;
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
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
public class StockServiceImpl implements StockService {
    private final StockRepository stockRepository;
    private final StockPriceRepository stockPriceRepository;
    private final KisStockPriceAdaptor kisStockPriceAdaptor;
    private final PeriodicStockSaver periodicSaver;

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
        List<StockPriceResponse> stockPriceResponseList = stockPriceRepository.findByStockAndBaseDateBetweenOrderByBaseDateDesc(stock, startDate, endDate)
                .stream().map(StockPriceResponse::of).toList();
        return StockInfoResponse.of(stock, stockPriceResponseList);
    }

    @Override
    public List<StockQueryResponse> searchStocks(String query) {
        List<Stock> stocks = stockRepository.findTop10ByStockNameContainingIgnoreCaseOrStockCdContainingIgnoreCaseOrIsinCdContainingIgnoreCaseOrderByMarketCapDesc(query, query, query);

        return stocks.stream()
                .map(StockQueryResponse::of)
                .toList();
    }

    @Override
    @Transactional
    public void updateCurrentStockData() {
        List<Stock> stocks = stockRepository.findAll();
        for (int stockIdx = 0; stockIdx <= stocks.size() / 30; stockIdx++) {
            List<Stock> stockList = stocks.subList(stockIdx * 30, Math.min((stockIdx + 1) * 30, stocks.size()));
            List<String> stockCdList = stockList.stream()
                    .map(Stock::getStockCd)
                    .toList();

            List<KisMultieStockCurrentPrice> output = kisStockPriceAdaptor.getMultiStockCurrentPrice(stockCdList).getOutput();

            List<StockCurrentPrice> stockCurrentPriceList = output.stream()
                    .map(currentPrice -> currentPrice.toEntity(LocalDate.now()))
                    .toList();

            for (int i = 0; i < Math.min(30, stockList.size()); i++)
                stockList.get(i).updateCurrentPriceInfo(stockCurrentPriceList.get(i));
        }
        log.info("[stock] 현재 주가 데이터 업데이트 완료");
    }

    @Override
    public void updatePeriodicStockData(LocalDate startDate, LocalDate endDate) {
        List<Stock> stocks = stockRepository.findAll();

        for (Stock stock : stocks) {
            for (LocalDate updateDate = (startDate.isBefore(stock.getListedDate()) ? stock.getListedDate() : startDate); updateDate.isBefore(endDate); updateDate = updateDate.plusDays(140)) {
                periodicSaver.saveOnePeriod(stock, updateDate, updateDate.plusDays(139));
            }

            log.info("[stock] 기간 주가 데이터 업데이트 완료: {} 진행률 {}%",
                    stock.getStockCd(),
                    (stocks.indexOf(stock) + 1) * 100 / stocks.size());
        }
    }

    @Override
    @Transactional
    public void forceUpdateStockData(String stockCd) {
        Stock stock = stockRepository.findByStockCd(stockCd)
                .orElseThrow(() -> new GeneralException(ErrorStatus.STOCK_NOT_FOUND));

        stockPriceRepository.deleteAllByStock(stock);
        for (LocalDate updateDate = LocalDate.now().minusYears(10).withMonth(1); updateDate.isBefore(LocalDate.now()); updateDate = updateDate.plusDays(140)) {
            periodicSaver.saveOnePeriod(stock, updateDate, updateDate.plusDays(139));
        }

        log.info("[stock] 강제 주가 데이터 업데이트 완료: {}", stock.getStockCd());
    }

    @Override
    @Transactional
    public void saveDailyStockData() {
        LocalDate today = LocalDate.now();

        Set<String> savedId = stockPriceRepository.findAllByBaseDate(today).stream()
                .map(sp -> sp.getStock().getStockCd())
                .collect(Collectors.toSet());
        List<Stock> stockList = stockRepository.findAll();

        List<StockPrice> savePriceList = new ArrayList<>();
        for (Stock stock : stockList) {
            if (savedId.contains(stock.getStockCd()))
                continue;
            if (stock.getCurrentPriceInfo() == null) {
                log.info("[stock] 일간 주가 데이터 저장 건너뜀: {} {} (현재가 정보 없음)", stock.getStockName(), stock.getStockCd());
                continue;
            }

            StockPrice stockPrice = stock.getCurrentPriceInfo().toStockPriceEntity();
            stockPrice.updateStock(stock);

            savePriceList.add(stockPrice);
        }
        stockPriceRepository.saveAll(savePriceList);
    }

    @Override
    public void updateAllStockPriceData() {
        List<Stock> stocks = stockRepository.findAll();

        for (Stock stock : stocks) {
            for (LocalDate updateDate = stock.getListedDate(); updateDate.isBefore(LocalDate.of(2015, 11, 2)); updateDate = updateDate.plusDays(140)) {
                periodicSaver.saveOnePeriod(stock, updateDate, updateDate.plusDays(139));
            }

            log.info("[stock] 전체 주가 데이터 업데이트 완료: {} 진행률 {}%",
                    stock.getStockCd(),
                    (stocks.indexOf(stock) + 1) * 100 / stocks.size());
        }
    }
}
