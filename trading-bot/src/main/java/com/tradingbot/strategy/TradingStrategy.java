package com.tradingbot.strategy;

import net.jacobpeterson.alpaca.openapi.marketdata.model.StockBar;
import java.util.List;

public interface TradingStrategy {
    Signal generateSignal(List<StockBar> bars);
    String getName();
}