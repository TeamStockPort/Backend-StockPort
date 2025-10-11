package com.stockport.server.stock.service;

import java.time.LocalDate;

public interface StockPriceService {
    public void fetchAndSaveStockPricesByBasDt(LocalDate basDt);
    public void fetchAndSaveStockPricesByIsinCd(String isinCd);
    public void fetchAndSaveAllStockPrices();
}
