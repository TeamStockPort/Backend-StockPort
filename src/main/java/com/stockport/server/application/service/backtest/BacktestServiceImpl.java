package com.stockport.server.application.service.backtest;

import com.stockport.server.application.controller.backtest.dto.request.AssetRequest;
import com.stockport.server.application.controller.backtest.dto.request.BacktestRequest;
import com.stockport.server.application.controller.backtest.dto.request.RebalanceCycle;
import com.stockport.server.application.controller.backtest.dto.response.BacktestResponse;
import com.stockport.server.application.controller.backtest.dto.response.PortfolioReturn;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class BacktestServiceImpl implements BacktestService {
    @Override
    public BacktestResponse runBacktest(BacktestRequest request) {
        return null;
    }
}
