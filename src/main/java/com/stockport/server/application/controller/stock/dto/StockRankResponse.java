package com.stockport.server.application.controller.stock.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class StockRankResponse extends StockInfoResponse {
    private Integer rank;
}
