package com.stockport.server.application.service.backtest;

import com.stockport.server.application.controller.backtest.dto.request.BacktestRequest;
import com.stockport.server.application.controller.backtest.dto.response.BacktestResponse;

public interface BacktestService {
    BacktestResponse runBacktest(BacktestRequest request);
    void validateRequest(BacktestRequest request);
}
