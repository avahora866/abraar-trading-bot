package com.tradingbot.strategy;

import net.jacobpeterson.alpaca.openapi.marketdata.model.StockBar;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class SmaCrossoverStrategy implements TradingStrategy {

    private final int shortWindow;
    private final int longWindow;

    public SmaCrossoverStrategy(
            @Value("${strategy.sma.short-window}") int shortWindow,
            @Value("${strategy.sma.long-window}") int longWindow) {
        this.shortWindow = shortWindow;
        this.longWindow = longWindow;
    }

    @Override
    public String getName() {
        return "SMA_Crossover";
    }

    @Override
    public Signal generateSignal(List<StockBar> bars) {
        if (bars == null || bars.size() < longWindow + 1) {
            return Signal.HOLD; // Not enough data
        }

        // Current period data
        List<StockBar> currentBars = bars.subList(bars.size() - longWindow, bars.size());
        double currentShortSma = calculateSma(currentBars.subList(currentBars.size() - shortWindow, currentBars.size()));
        double currentLongSma = calculateSma(currentBars);

        // Previous period data for crossover detection
        List<StockBar> prevBars = bars.subList(bars.size() - longWindow - 1, bars.size() - 1);
        double prevShortSma = calculateSma(prevBars.subList(prevBars.size() - shortWindow, prevBars.size()));
        double prevLongSma = calculateSma(prevBars);

        // Use standard double comparison
        if (prevShortSma <= prevLongSma && currentShortSma > currentLongSma) {
            return Signal.BUY; // Bullish crossover
        } else if (prevShortSma >= prevLongSma && currentShortSma < currentLongSma) {
            return Signal.SELL; // Bearish crossover
        }

        return Signal.HOLD;
    }

    private double calculateSma(List<StockBar> bars) {
        if (bars == null || bars.isEmpty()) {
            return 0.0;
        }

        // Use mapToDouble and getC()
        return bars.stream()
                .mapToDouble(StockBar::getC) // Use getC() which returns Double
                .average()
                .orElse(0.0);
    }
}