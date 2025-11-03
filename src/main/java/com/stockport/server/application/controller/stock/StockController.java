package com.stockport.server.application.controller.stock;

import com.stockport.server.application.controller.stock.dto.StockInfoResponse;
import com.stockport.server.application.controller.stock.dto.StockQueryResponse;
import com.stockport.server.application.controller.stock.dto.StockRankResponse;
import com.stockport.server.application.service.stock.StockService;
import com.stockport.server.global.apipayload.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Tag(name = "Stock Data", description = "주식 데이터 API")
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/stock")
public class StockController {
    private final StockService stockService;

    @GetMapping("/market-cap") // 주식 종목 시가총액 순 조회
    public ApiResponse<Page<StockRankResponse>> getStocksByMarketCap(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size
    ) {
        Page<StockRankResponse> stockPage = stockService.getStocksByMarketCap(PageRequest.of(page, size));
        return ApiResponse.onSuccess(stockPage);
    }

    @GetMapping("/{stockCode}") // 주식 종목 정보 조회
    public ApiResponse<StockInfoResponse> getStockInfo(
            @PathVariable String stockCode,
            @RequestParam String startDate,
            @RequestParam String endDate
    ) {
        StockInfoResponse response = stockService.getStockInfo(stockCode, startDate, endDate);
        return ApiResponse.onSuccess(response);
    }

    @GetMapping("")// 주식 종목 검색
    public ApiResponse<List<StockQueryResponse>> searchStocks(
            @RequestParam String query
    ) {
        List<StockQueryResponse> response = stockService.searchStocks(query);
        return ApiResponse.onSuccess(response);
    }
}
