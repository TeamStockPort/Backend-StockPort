package com.stockport.server.application.service.backtest;

import com.stockport.server.application.controller.backtest.dto.request.AssetRequest;
import com.stockport.server.application.controller.backtest.dto.request.BacktestRequest;
import com.stockport.server.application.controller.backtest.dto.request.RebalanceCycle;
import com.stockport.server.application.controller.backtest.dto.response.BacktestResponse;
import com.stockport.server.application.controller.backtest.dto.response.PortfolioValue;
import com.stockport.server.application.controller.backtest.dto.response.SummaryReport;
import com.stockport.server.domain.indexData.constant.MarketType;
import com.stockport.server.domain.indexData.entity.IndexData;
import com.stockport.server.domain.indexData.repository.IndexDataRepository;
import com.stockport.server.domain.stock.entity.StockPrice;
import com.stockport.server.domain.stock.repository.StockPriceRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class BacktestServiceImpl implements BacktestService {
    private final StockPriceRepository stockPriceRepository;
    private final IndexDataRepository indexDataRepository;

    @Override
    public BacktestResponse runBacktest(BacktestRequest request) {
        List<PortfolioValue> kospiReturns = caculateIndexReturns(request, MarketType.KOSPI);
        List<PortfolioValue> kosdaqReturns = caculateIndexReturns(request, MarketType.KOSDAQ);
        List<PortfolioValue> portfolioReturns = calculatePortfolioReturns(request);
        List<PortfolioValue> monthlyDrawdonws = caculateMDD(portfolioReturns);

        return BacktestResponse.builder()
                .kospiSummary(null)
                .kosdaqSummary(null)
                .portfolioSummary(null)
                .monthlyAssets(calcuateMonthlyAssets(portfolioReturns))
                .monthlyDrawdowns(monthlyDrawdonws)
                .monthlyReturns(caculateMonthlyReturns(portfolioReturns))
                .build();
    }

    private SummaryReport caculateSummaryReport() {
        return null;
    }

    private List<PortfolioValue> calcuateMonthlyAssets(List<PortfolioValue> protfolioReturns) {
        LocalDate monthBoundaryDate = protfolioReturns.get(0).getDate().withDayOfMonth(1);
        List<PortfolioValue> monthlyAssetList = new ArrayList<>();

        for (PortfolioValue pv : protfolioReturns) {
            if (pv.getDate().isBefore(monthBoundaryDate)) continue;

            monthlyAssetList.add(PortfolioValue.create(pv.getDate(), pv.getValue()));
            monthBoundaryDate = monthBoundaryDate.plusMonths(1).withDayOfMonth(1);
        }
        return monthlyAssetList;
    }

    private List<PortfolioValue> caculateMonthlyReturns(List<PortfolioValue> portfolioReturns) {
        LocalDate monthBoundaryDate = portfolioReturns.get(0).getDate().plusMonths(1).withDayOfMonth(1);
        List<PortfolioValue> monthlyReturnList = new ArrayList<>();

        BigDecimal monthStartValue = portfolioReturns.get(0).getValue();
        for (PortfolioValue pv : portfolioReturns) {
            if (pv.getDate().isBefore(monthBoundaryDate)) continue;

            BigDecimal monthEndValue = portfolioReturns.get(portfolioReturns.indexOf(pv) - 1).getValue();
            BigDecimal monthlyReturn = monthEndValue.subtract(monthStartValue)
                    .multiply(BigDecimal.valueOf(100))
                    .divide(monthStartValue, 2, RoundingMode.HALF_EVEN);
            monthlyReturnList.add(PortfolioValue.create(monthBoundaryDate, monthlyReturn));

            monthBoundaryDate = monthBoundaryDate.plusMonths(1).withDayOfMonth(1);
            monthStartValue = pv.getValue();
        }
        return monthlyReturnList;
    }

    private List<PortfolioValue> caculateMDD(List<PortfolioValue> portfolioReturns) {
        LocalDate monthBoundaryDate = portfolioReturns.get(0).getDate().plusMonths(1).withDayOfMonth(1);
        List<PortfolioValue> mddList = new ArrayList<>();

        BigDecimal peak = BigDecimal.ZERO, mdd = BigDecimal.ZERO;
        for (PortfolioValue pv : portfolioReturns) {
            if (pv.getDate().isAfter(monthBoundaryDate)) {
                monthBoundaryDate = monthBoundaryDate.plusMonths(1).withDayOfMonth(1);
                mddList.add(PortfolioValue.create(monthBoundaryDate, mdd));
                peak = BigDecimal.ZERO; mdd = BigDecimal.ZERO;
            }
            if (pv.getValue().compareTo(peak) > 0) {
                peak = pv.getValue();
                continue;
            }

            BigDecimal drawdown = peak.subtract(pv.getValue())
                    .multiply(BigDecimal.valueOf(100))
                    .divide(peak, 2, RoundingMode.HALF_EVEN)
                    .negate();

            if (drawdown.compareTo(mdd) > 0)
                mdd = drawdown;
        }
        return mddList;
    }

    private List<PortfolioValue> caculateIndexReturns(BacktestRequest request, MarketType marketType) {
        LocalDate startDate = request.getStartDate();
        LocalDate endDate = request.getEndDate();
        BigDecimal initialCapital = BigDecimal.valueOf(request.getInitialCapital()).setScale(2, RoundingMode.HALF_EVEN);

        List<IndexData> indexDataList = indexDataRepository.findAllByMarketTypeAndBaseDateBetweenOrderByBaseDateAsc(
                marketType, startDate, endDate
        );

        List<PortfolioValue> portfolioValueList = new ArrayList<>();
        BigDecimal stockQuantity = initialCapital.divide(indexDataList.get(0).getClosePrice(), RoundingMode.HALF_EVEN);
        BigDecimal remainingCash = initialCapital.subtract(stockQuantity.multiply(indexDataList.get(0).getClosePrice()));
        for (IndexData indexData : indexDataList) {
            portfolioValueList.add(PortfolioValue.create(
                    indexData.getBaseDate(),
                    stockQuantity.multiply(indexData.getClosePrice())
                            .add(remainingCash)
                            .setScale(2, RoundingMode.HALF_EVEN)
            ));
        }

        return portfolioValueList;
    }

    private List<PortfolioValue> calculatePortfolioReturns(BacktestRequest request) {
        LocalDate startDate = request.getStartDate();
        LocalDate endDate = request.getEndDate();
        BigDecimal capital = BigDecimal.valueOf(request.getInitialCapital()).setScale(2, RoundingMode.HALF_EVEN);
        List<AssetRequest> assets = request.getAssets();

        List<List<StockPrice>> dailyStockPriceLists = getDailyStockPriceList(assets, startDate, endDate);
        LocalDate currentDate = startDate;
        LocalDate lastRebalanceDate = startDate.minusYears(2);
        List<BigDecimal> stockQuantityList = calculateStockQuantities(capital, assets, dailyStockPriceLists.get(0));
        BigDecimal remaingCash = caculateRemainingCash(capital, stockQuantityList, dailyStockPriceLists.get(0));
        List<PortfolioValue> portfolioValueList = new ArrayList<>();

        for (List<StockPrice> dailyStockPriceList : dailyStockPriceLists) {
            capital = caculatePortfolioValue(stockQuantityList, dailyStockPriceList)
                    .add(remaingCash)
                    .setScale(2, RoundingMode.HALF_EVEN);
            currentDate = dailyStockPriceList.get(0).getBaseDate();

            if (checkRebalance(currentDate, lastRebalanceDate, request.getRebalanceCycle())) {
                stockQuantityList = calculateStockQuantities(capital, assets, dailyStockPriceList);
                remaingCash = caculateRemainingCash(capital, stockQuantityList, dailyStockPriceList);
                lastRebalanceDate = currentDate;
            }

            portfolioValueList.add(PortfolioValue.create(currentDate, capital));
        }

        return portfolioValueList;
    }

    private BigDecimal caculateRemainingCash(BigDecimal initialCapital, List<BigDecimal> stockQuantityList, List<StockPrice> dailyStockPriceList) {
        BigDecimal usedCapital = BigDecimal.ZERO;
        for (int i = 0; i < stockQuantityList.size(); i++) {
            BigDecimal stockQuantity = stockQuantityList.get(i);
            StockPrice stockPrice = dailyStockPriceList.get(i);
            usedCapital = usedCapital.add(stockQuantity.multiply(stockPrice.getClosePrice()));
        }
        return initialCapital.subtract(usedCapital);
    }

    private BigDecimal caculatePortfolioValue(List<BigDecimal> stockQuantityList, List<StockPrice> dailyStockPriceList) {
        BigDecimal portfolioValue = BigDecimal.ZERO;
        for (int i = 0; i < stockQuantityList.size(); i++) {
            BigDecimal stockQuantity = stockQuantityList.get(i);
            StockPrice stockPrice = dailyStockPriceList.get(i);
            portfolioValue = portfolioValue.add(stockQuantity.multiply(stockPrice.getClosePrice()));
        }
        return portfolioValue;
    }

    private List<BigDecimal> calculateStockQuantities(BigDecimal capital, List<AssetRequest> assets, List<StockPrice> stockPriceListByStocks) {
        List<BigDecimal> stockQuantityList = new ArrayList<>();
        for (int i = 0; i < assets.size(); i++) {
            AssetRequest asset = assets.get(i);
            StockPrice stockPrice = stockPriceListByStocks.get(i);
            BigDecimal allocation = capital
                    .multiply(BigDecimal.valueOf(asset.getWeight()))
                    .divide(BigDecimal.valueOf(100), RoundingMode.DOWN);
            BigDecimal stockQuantity = allocation.divide(stockPrice.getClosePrice(), RoundingMode.DOWN);
            stockQuantityList.add(stockQuantity);
        }
        return stockQuantityList;
    }

    private List<List<StockPrice>> getDailyStockPriceList(List<AssetRequest> assets, LocalDate startDate, LocalDate endDate) {
        List<List<StockPrice>> stockPriceLists = new ArrayList<>();
        for (AssetRequest asset : assets) {
            List<StockPrice> stockPriceListByStock = stockPriceRepository.findByStockStockCdAndBaseDateBetweenOrderByBaseDateAsc(
                    asset.getStockCd(), startDate, endDate
            );
            stockPriceLists.add(stockPriceListByStock);
        }

        List<List<StockPrice>> dailyStockPriceList = new ArrayList<>();
        for (int i = 0; i < stockPriceLists.get(0).size(); i++) {
            List<StockPrice> dailyPrices = new ArrayList<>();
            for (List<StockPrice> stockPrices : stockPriceLists) {
                dailyPrices.add(stockPrices.get(i));
            }
            dailyStockPriceList.add(dailyPrices);
        }

        return dailyStockPriceList;
    }

    private boolean checkRebalance(LocalDate currentDate, LocalDate lastRebalanceDate, RebalanceCycle rebalanceCycle) {
        return switch (rebalanceCycle) {
            case MONTHLY -> currentDate.isAfter(lastRebalanceDate.plusMonths(1));
            case QUARTERLY -> currentDate.isAfter(lastRebalanceDate.plusMonths(3));
            case YEARLY -> currentDate.isAfter(lastRebalanceDate.plusYears(1));
        };
    }
}
