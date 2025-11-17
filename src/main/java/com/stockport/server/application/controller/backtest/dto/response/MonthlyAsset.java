package com.stockport.server.application.controller.backtest.dto.response;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
public class MonthlyAsset {
    private LocalDate date;
    private long portfolioValue;
}