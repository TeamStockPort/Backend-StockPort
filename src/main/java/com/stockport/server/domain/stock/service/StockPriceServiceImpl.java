package com.stockport.server.domain.stock.service;

import com.stockport.server.domain.stock.repository.StockPriceRepository;
import com.stockport.server.domain.stock.repository.StockRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;

@Service
@RequiredArgsConstructor
public class StockPriceServiceImpl implements StockPriceService {
    private final StockPriceRepository stockPriceRepository;
    private final StockRepository stockRepository;

    @Override
    public void fetchAndSaveStockPricesByBasDt(LocalDate basDt) {

    }

    @Override
    public void fetchAndSaveStockPricesByIsinCd(String isinCd) {

    }

    @Override
    public void fetchAndSaveAllStockPrices() {

    }
}
