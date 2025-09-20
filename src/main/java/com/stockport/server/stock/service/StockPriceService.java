package com.stockport.server.stock.service;

public interface StockPriceService {
    public void fetchAndSaveStockPricesByBasDt(String basDt);
    public void fetchAndSaveStockPricesByIsinCd(String isinCd);
    public void fetchAndSaveAllStockPrices();
}
