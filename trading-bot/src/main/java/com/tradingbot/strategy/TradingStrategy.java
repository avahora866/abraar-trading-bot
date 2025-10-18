package com.tradingbot.strategy;

// Import for the new StockBar location
import net.jacobpeterson.alpaca.openapi.marketdata.model.StockBar;
import java.util.List;

public interface TradingStrategy {
    Signal generateSignal(List<StockBar> bars);
    String getName();
}