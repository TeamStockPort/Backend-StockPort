package com.stockport.server.application.service.indexData;

import com.stockport.server.application.controller.IndexData.dto.IndexDataResponse;
import com.stockport.server.domain.indexData.constant.MarketType;

import java.time.LocalDate;
import java.util.List;

public interface IndexDataService {
    public void updateCurrentIndexData(MarketType marketType);
    public void updateHistoricalIndexData(MarketType marketType);

    public IndexDataResponse getCurrentIndexData(MarketType marketType);
    public List<IndexDataResponse> getPeriodIndexData(MarketType marketType, LocalDate startDate, LocalDate endDate);
}
