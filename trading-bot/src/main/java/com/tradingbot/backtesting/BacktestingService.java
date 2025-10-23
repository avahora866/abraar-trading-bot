package com.tradingbot.backtesting;

import com.tradingbot.alpaca.AlpacaService;
import com.tradingbot.strategy.Signal;
import com.tradingbot.strategy.SmaCrossoverStrategy;
import net.jacobpeterson.alpaca.openapi.marketdata.model.StockBar;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.ZonedDateTime;
import java.util.List;

@Service
public class BacktestingService {

    private static final Logger logger = LoggerFactory.getLogger(BacktestingService.class);

    @Autowired
    private AlpacaService alpacaService;

    @Autowired
    private SmaCrossoverStrategy strategy;

    @Value("${strategy.sma.long-window}")
    private int longWindow;

    public BacktestResult run(String symbol, ZonedDateTime start, ZonedDateTime end) {
        
        List<StockBar> allBars = alpacaService.getBars(
                symbol,
                start,
                end,
                10000,
                "1Day" // Use the String literal
        );

        if (allBars == null || allBars.size() <= longWindow + 1) {
            logger.warn("Not enough data to run backtest for {}", symbol);
            return new BacktestResult(symbol, strategy.getName(), 0, 0, 0, 0, 0);
        }

        double initialCapital = 10000.0;
        double capital = initialCapital;
        double shares = 0.0;
        int trades = 0;

        for (int i = longWindow + 1; i < allBars.size(); i++) {
            List<StockBar> historicalSlice = allBars.subList(0, i);
            Signal signal = strategy.generateSignal(historicalSlice);

            double currentPrice = allBars.get(i).getC();

            if (signal == Signal.BUY && capital > currentPrice) {
                shares = capital / currentPrice; // Buy as many shares as possible
                capital = 0.0;
                trades++;
            } else if (signal == Signal.SELL && shares > 0) {
                capital = shares * currentPrice; // Sell all shares
                shares = 0.0;
                trades++;
            }
        }

        // Final valuation at the end of the test
        double finalCapital = capital;
        if (shares > 0) {
            double lastPrice = allBars.get(allBars.size() - 1).getC();
            finalCapital += shares * lastPrice;
        }

        double pnl = finalCapital - initialCapital;
        double pnlPercent = (pnl / initialCapital) * 100.0;

        return new BacktestResult(
                symbol,
                strategy.getName(),
                initialCapital,
                finalCapital,
                pnl,
                pnlPercent,
                trades
        );
    }
}