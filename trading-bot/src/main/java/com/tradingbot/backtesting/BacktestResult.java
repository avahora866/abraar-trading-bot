package com.tradingbot.backtesting;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class BacktestResult {
    private String symbol;
    private String strategyName;
    private double initialCapital;
    private double finalCapital;
    private double profitLoss;
    private double profitLossPercent;
    private int totalTrades;
}