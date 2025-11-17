package com.stockport.server.application.controller.backtest.dto.request;


import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AssetRequest {
    private String stockCd;
    private int weight;
}