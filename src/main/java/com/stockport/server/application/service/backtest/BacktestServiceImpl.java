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
import java.time.temporal.ChronoUnit;
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
        List<PortfolioValue> kospiValues = calculateIndexReturns(request, MarketType.KOSPI);
        List<PortfolioValue> kosdaqValues = calculateIndexReturns(request, MarketType.KOSDAQ);
        List<PortfolioValue> portfolioValues = calculatePortfolioReturns(request);

        return BacktestResponse.builder()
                .kospiSummary(calculateSummaryReport(kospiValues, "KOSPI"))
                .kosdaqSummary(calculateSummaryReport(kosdaqValues, "KOSDAQ"))
                .portfolioSummary(calculateSummaryReport(portfolioValues, "PORTFOLIO"))
                .monthlyAssets(calcuateMonthlyAssets(portfolioValues))
                .monthlyDrawdowns(calculateMDD(portfolioValues))
                .monthlyReturns(calculateMonthlyReturns(portfolioValues))
                .build();
    }

    private SummaryReport calculateSummaryReport(List<PortfolioValue> values, String portfolioName) {
        List<BigDecimal> dailyReturns = calculateDailyReturns(values);
        BigDecimal avgDailyReturn = calculateAvgDailyReturn(dailyReturns);

        BigDecimal volatility = calculateVolatility(dailyReturns, avgDailyReturn, values);
        BigDecimal sharpeRatio = calculateSharpeRatio(values, dailyReturns, avgDailyReturn, volatility);
        BigDecimal sortinoRatio = calculateSortinoRatio(dailyReturns, avgDailyReturn);
        return SummaryReport.builder()
                .portfolioName(portfolioName)
                .initialCapital(values.get(0).getValue())
                .finalCapital(values.get(values.size() - 1).getValue())
                .cagr(calculateCagr(values))
                .maxDrawdown(calculateTotalMDD(values))
                .volatility(volatility)
                .sharpeRatio(sharpeRatio)
                .sortinoRatio(sortinoRatio)
                .build();
    }

    private BigDecimal calculateAvgDailyReturn(List<BigDecimal> dailyReturns) {
        BigDecimal sum = dailyReturns.stream()
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        return sum.divide(
                BigDecimal.valueOf(dailyReturns.size()),
                10,
                RoundingMode.HALF_UP
        );
    }

    private List<BigDecimal> calculateDailyReturns(List<PortfolioValue> values) {
        List<BigDecimal> dailyReturns = new ArrayList<>();

        for (int i = 1; i < values.size(); i++) {
            BigDecimal prev = values.get(i - 1).getValue();
            BigDecimal curr = values.get(i).getValue();

            BigDecimal dailyReturn = curr.subtract(prev)
                    .divide(prev, 10, RoundingMode.HALF_UP);

            dailyReturns.add(dailyReturn);
        }
        return dailyReturns;
    }

    private BigDecimal calculateSortinoRatio(List<BigDecimal> dailyReturns, BigDecimal avgDailyReturn) {
        if (dailyReturns.isEmpty())
            return BigDecimal.ZERO;

        // Downside Deviation 계산 (음수 수익률만 사용)
        BigDecimal downsideSum = BigDecimal.ZERO;
        int downsideCount = 0;

        for (BigDecimal r : dailyReturns) {
            if (r.compareTo(BigDecimal.ZERO) < 0) {  // 음수 리턴만
                downsideSum = downsideSum.add(r.multiply(r));
                downsideCount++;
            }
        }

        if (downsideCount == 0)
            return BigDecimal.ZERO; // 손실이 없으면 Sortino = 0 또는 매우 높음 (여기서는 0 반환)

        BigDecimal downsideVariance = downsideSum.divide(
                BigDecimal.valueOf(downsideCount),
                10,
                RoundingMode.HALF_UP
        );

        BigDecimal downsideDeviation = BigDecimal.valueOf(
                Math.sqrt(downsideVariance.doubleValue())
        ).setScale(10, RoundingMode.HALF_UP);

        // 연율화된 Downside Deviation = × sqrt(252)
        BigDecimal annualizedDownsideDeviation = downsideDeviation
                .multiply(BigDecimal.valueOf(Math.sqrt(252)))
                .setScale(10, RoundingMode.HALF_UP);

        if (annualizedDownsideDeviation.compareTo(BigDecimal.ZERO) == 0)
            return BigDecimal.ZERO;

        // Sortino Ratio = (avgDailyReturn × 252) / annualizedDownsideDeviation
        BigDecimal annualizedReturn = avgDailyReturn.multiply(BigDecimal.valueOf(252));

        return annualizedReturn.divide(annualizedDownsideDeviation, 4, RoundingMode.HALF_UP);
    }

    private BigDecimal calculateSharpeRatio(List<PortfolioValue> values, List<BigDecimal> dailyReturns, BigDecimal avgDailyReturn, BigDecimal volatility) {
        if (dailyReturns.isEmpty())
            return BigDecimal.ZERO;


        // 연율화된 평균 수익률 = avgDailyReturn * 252
        BigDecimal annualizedReturn = avgDailyReturn
                .multiply(BigDecimal.valueOf(252));

        if (volatility.compareTo(BigDecimal.ZERO) == 0)
            return BigDecimal.ZERO;

        // Sharpe Ratio = annualizedReturn / volatility
        return annualizedReturn.divide(volatility, 4, RoundingMode.HALF_UP);
    }

    private BigDecimal calculateVolatility(List<BigDecimal> dailyReturns, BigDecimal avgDailyReturns, List<PortfolioValue> values) {
        if (values.size() < 2)
            return BigDecimal.ZERO;

        // 분산 계산
        BigDecimal varianceSum = BigDecimal.ZERO;
        for (BigDecimal r : dailyReturns) {
            BigDecimal diff = r.subtract(avgDailyReturns);
            varianceSum = varianceSum.add(diff.multiply(diff));
        }

        BigDecimal variance = varianceSum.divide(
                BigDecimal.valueOf(dailyReturns.size()),
                10,
                RoundingMode.HALF_UP
        );

        // 표준편차 = sqrt(variance)
        BigDecimal stdDev = BigDecimal.valueOf(Math.sqrt(variance.doubleValue()))
                .setScale(10, RoundingMode.HALF_UP);

        // 연율화 변동성 = stdDev × sqrt(252)
        return stdDev
                .multiply(BigDecimal.valueOf(Math.sqrt(252)))
                .setScale(4, RoundingMode.HALF_UP);
    }

    private BigDecimal calculateTotalMDD(List<PortfolioValue> values) {
        BigDecimal peak = values.get(0).getValue();
        BigDecimal maxDrawdown = BigDecimal.ZERO;

        for (PortfolioValue pv : values) {
            if (pv.getValue().compareTo(peak) > 0) {
                peak = pv.getValue();
                continue;
            }

            BigDecimal drawdown = peak.subtract(pv.getValue())
                    .multiply(BigDecimal.valueOf(100))
                    .divide(peak, 2, RoundingMode.HALF_EVEN);

            if (drawdown.compareTo(maxDrawdown) > 0)
                maxDrawdown = drawdown;
        }
        return maxDrawdown.negate();
    }

    private BigDecimal calculateCagr(List<PortfolioValue> values) {

        BigDecimal beginningValue = values.get(0).getValue();
        BigDecimal endingValue = values.get(values.size() - 1).getValue();

        LocalDate startDate = values.get(0).getDate();
        LocalDate endDate = values.get(values.size() - 1).getDate();
        long days = ChronoUnit.DAYS.between(startDate, endDate);

        // 연수(years) = 일수 / 365
        BigDecimal years = BigDecimal.valueOf(days)
                .divide(BigDecimal.valueOf(365), 10, RoundingMode.HALF_UP);

        // endingValue / beginningValue
        BigDecimal ratio = endingValue.divide(beginningValue, 10, RoundingMode.HALF_UP);

        // CAGR = ratio ^ (1 / years) - 1
        double cagrDouble = Math.pow(ratio.doubleValue(), 1.0 / years.doubleValue()) - 1;

        return BigDecimal.valueOf(cagrDouble)
                .multiply(BigDecimal.valueOf(100))
                .setScale(4, RoundingMode.HALF_UP);  // 소수 4자리까지 예시
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

    private List<PortfolioValue> calculateMonthlyReturns(List<PortfolioValue> portfolioReturns) {
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

    private List<PortfolioValue> calculateMDD(List<PortfolioValue> portfolioReturns) {
        LocalDate monthBoundaryDate = portfolioReturns.get(0).getDate().plusMonths(1).withDayOfMonth(1);
        List<PortfolioValue> mddList = new ArrayList<>();

        BigDecimal peak = BigDecimal.ZERO, mdd = BigDecimal.ZERO;
        for (PortfolioValue pv : portfolioReturns) {
            if (pv.getDate().isAfter(monthBoundaryDate)) {
                mddList.add(PortfolioValue.create(monthBoundaryDate, mdd));
                monthBoundaryDate = monthBoundaryDate.plusMonths(1).withDayOfMonth(1);
            }
            if (pv.getValue().compareTo(peak) > 0) {
                peak = pv.getValue();
                mdd = BigDecimal.ZERO;
                continue;
            }

            BigDecimal drawdown = peak.subtract(pv.getValue())
                    .multiply(BigDecimal.valueOf(100))
                    .divide(peak, 2, RoundingMode.HALF_EVEN);

            if (drawdown.compareTo(mdd) > 0)
                mdd = drawdown.negate();
        }
        return mddList;
    }

    private List<PortfolioValue> calculateIndexReturns(BacktestRequest request, MarketType marketType) {
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
        BigDecimal remaingCash = calculateRemainingCash(capital, stockQuantityList, dailyStockPriceLists.get(0));
        List<PortfolioValue> portfolioValueList = new ArrayList<>();

        for (List<StockPrice> dailyStockPriceList : dailyStockPriceLists) {
            capital = calculatePortfolioValue(stockQuantityList, dailyStockPriceList)
                    .add(remaingCash)
                    .setScale(2, RoundingMode.HALF_EVEN);
            currentDate = dailyStockPriceList.get(0).getBaseDate();

            if (checkRebalance(currentDate, lastRebalanceDate, request.getRebalanceCycle())) {
                stockQuantityList = calculateStockQuantities(capital, assets, dailyStockPriceList);
                remaingCash = calculateRemainingCash(capital, stockQuantityList, dailyStockPriceList);
                lastRebalanceDate = currentDate;
            }

            portfolioValueList.add(PortfolioValue.create(currentDate, capital));
        }

        return portfolioValueList;
    }

    private BigDecimal calculateRemainingCash(BigDecimal initialCapital, List<BigDecimal> stockQuantityList, List<StockPrice> dailyStockPriceList) {
        BigDecimal usedCapital = BigDecimal.ZERO;
        for (int i = 0; i < stockQuantityList.size(); i++) {
            BigDecimal stockQuantity = stockQuantityList.get(i);
            StockPrice stockPrice = dailyStockPriceList.get(i);
            usedCapital = usedCapital.add(stockQuantity.multiply(stockPrice.getClosePrice()));
        }
        return initialCapital.subtract(usedCapital);
    }

    private BigDecimal calculatePortfolioValue(List<BigDecimal> stockQuantityList, List<StockPrice> dailyStockPriceList) {
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
