package com.stockport.server.stock.service;

import com.stockport.server.stock.client.StockPriceApiClient;
import com.stockport.server.stock.domain.Stock;
import com.stockport.server.stock.domain.StockPrice;
import com.stockport.server.stock.dto.StockPriceDto;
import com.stockport.server.stock.repository.StockPriceRepository;
import com.stockport.server.stock.repository.StockRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class StockPriceServiceImpl implements StockPriceService {
    private final StockPriceApiClient stockPriceApiClient;
    private final StockPriceRepository stockPriceRepository;
    private final StockRepository stockRepository;

    @Override
    public void fetchAndSaveStockPricesByBasDt(String basDt) {
        var stockPrices = stockPriceApiClient.getAllStockPricesByDate(basDt);

        for (var dto : stockPrices) {
            Stock stock = stockRepository.findByIsinCd(dto.getIsinCd())
                    .orElseThrow(() -> new IllegalArgumentException("Invalid ISIN code: " + dto.getIsinCd()));

            StockPrice stockPrice = StockPriceDto.toEntity(dto, stock);
            stockPriceRepository.save(stockPrice);
        }
    }

    @Override
    public void fetchAndSaveStockPricesByIsinCd(String isinCd) {
        var stockPriceDtoList = stockPriceApiClient.getStockPriceHistory(isinCd);

        Stock stock = stockRepository.findByIsinCd(isinCd)
                .orElseThrow(() -> new IllegalArgumentException("Invalid ISIN code: " + isinCd));

        List<StockPrice> stockPriceList = stockPriceDtoList.stream()
                .map(dto -> StockPriceDto.toEntity(dto, stock))
                .toList();

        stockPriceRepository.saveAll(stockPriceList);
    }

    @Override
    public void fetchAndSaveAllStockPrices() {
        var StockList = stockRepository.findAll();
        for (var stock : StockList) {
            fetchAndSaveStockPricesByIsinCd(stock.getIsinCd());
        }
    }
}
