package com.stockport.server.application.controller.backtest.dto.response;

import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDate;

@Getter
@Setter
public class MonthlyDrawdown {
    private LocalDate date;
    private BigDecimal drawdown;
}
