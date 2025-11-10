package com.stockport.server.application.controller.stock;

import com.stockport.server.application.controller.stock.dto.StockInfoResponse;
import com.stockport.server.application.controller.stock.dto.StockQueryResponse;
import com.stockport.server.application.controller.stock.dto.StockRankResponse;
import com.stockport.server.application.service.stock.StockService;
import com.stockport.server.global.apipayload.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import lombok.RequiredArgsConstructor;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;
import java.util.List;

import static org.springframework.data.domain.Sort.Direction.DESC;

@Tag(name = "Stock Data", description = "주식 데이터 API")
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/stock")
public class StockController {
    private final StockService stockService;

    @GetMapping("/market-cap")
    @Operation(
            summary = "시가총액 순 주식 목록 조회",
            description = "시가총액 순으로 정렬된 주식 목록을 페이지네이션하여 조회합니다."
    )
    public ApiResponse<Page<StockRankResponse>> getStocksByMarketCap(
            @ParameterObject @PageableDefault(size = 20, sort = "marketCap", direction = DESC) Pageable pageable
    ) {
        return ApiResponse.onSuccess(stockService.getStocksByMarketCap(pageable));
    }

    @GetMapping("/{stockCode}")
    @Operation(
            summary = "단일 종목의 상세 정보 및 기간별 가격 조회",
            description = "특정 주식 종목의 상세 정보와 지정한 기간 내 가격 정보를 조회합니다."
    )
    public ApiResponse<StockInfoResponse> getStockInfo(
            @Parameter(description = "주식 종목 코드", example = "005930")
            @PathVariable String stockCode,
            @Parameter(description = "조회 시작 날짜 (yyyy-MM-dd)", example = "2024-01-01")
            @RequestParam LocalDate startDate,
            @Parameter(description = "조회 종료 날짜 (yyyy-MM-dd)", example = "2024-06-01")
            @RequestParam LocalDate endDate
    ) {
        StockInfoResponse response = stockService.getStockInfo(stockCode, startDate, endDate);
        return ApiResponse.onSuccess(response);
    }

    @GetMapping("")
    @Operation(
            summary = "주식 이름 또는 코드로 검색",
            description = "주식 이름 또는 코드를 입력하여 관련 종목을 검색합니다."
    )
    public ApiResponse<List<StockQueryResponse>> searchStocks(
            @Parameter(description = "검색어(주식 이름 또는 코드)", example = "삼성전자")
            @RequestParam String query
    ) {
        List<StockQueryResponse> response = stockService.searchStocks(query);
        return ApiResponse.onSuccess(response);
    }
}