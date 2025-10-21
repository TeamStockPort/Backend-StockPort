package com.stockport.server.stock.service;

import com.stockport.server.stock.domain.Stock;
import com.stockport.server.stock.domain.StockPrice;
import com.stockport.server.stock.dto.StockPriceDto;
import com.stockport.server.stock.repository.StockPriceRepository;
import com.stockport.server.stock.repository.StockRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;

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
