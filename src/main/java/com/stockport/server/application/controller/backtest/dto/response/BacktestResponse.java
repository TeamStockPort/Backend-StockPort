package com.stockport.server.application.controller.backtest.dto.response;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class BacktestResponse {
    private List<SummaryReport> summaryReport;
    private List<MonthlyAsset> monthlyAssets;
    private List<MonthlyDrawdown> monthlyDrawdowns;
    private List<PortfolioReturn> annualReturns;
}