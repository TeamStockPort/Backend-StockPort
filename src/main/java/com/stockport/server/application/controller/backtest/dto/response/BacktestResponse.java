package com.stockport.server.application.controller.backtest.dto.response;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@Builder
public class BacktestResponse {
    private List<SummaryReport> summaryReport;
    private List<MonthlyAsset> monthlyAssets;
    private List<MonthlyDrawdown> monthlyDrawdowns;
    private List<PortfolioReturn> annualReturns;
}