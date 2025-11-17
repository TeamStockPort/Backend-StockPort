package com.stockport.server.application.controller.backtest.dto.response;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@Builder
public class BacktestResponse {
    private SummaryReport kospiSummary;
    private SummaryReport kosdaqSummary;
    private SummaryReport portfolioSummary;
    private List<PortfolioValue> monthlyDrawdowns;
    private List<PortfolioValue> monthlyAssets;
    private List<PortfolioValue> monthlyReturns;
}