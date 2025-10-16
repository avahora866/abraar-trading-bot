package org.example.backtest;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public class BasicPortfolio implements Portfolio {

    private final String symbol;
    private final double initialCapital;
    private double cash;
    private int currentHolding = 0;
    private double currentMarketValue = 0.0;
    private double totalValue;
    private int totalTrades = 0;
    
    // A simple way to track unrealized and realized PnL
    private final Map<String, Double> positions = new HashMap<>();

    public BasicPortfolio(String symbol, double initialCapital) {
        this.symbol = symbol;
        this.initialCapital = initialCapital;
        this.cash = initialCapital;
        this.totalValue = initialCapital;
    }

    @Override
    public void onMarketEvent(MarketEvent event) {
        // Update total value based on the latest market price
        if (event.symbol().equals(symbol)) {
            this.currentMarketValue = currentHolding * event.data().getC();
            this.totalValue = this.cash + this.currentMarketValue;
        }
    }

    @Override
    public Optional<OrderEvent> onSignalEvent(SignalEvent event) {
        if (!event.symbol().equals(symbol)) {
            return Optional.empty();
        }

        int quantityToTrade = 100; // Fixed quantity for simplicity

        if ("BUY".equals(event.direction()) && currentHolding == 0) {
            // Buy signal and we have no position
            double cost = quantityToTrade * event.price();
            if (cash >= cost) {
                return Optional.of(new OrderEvent(symbol, "BUY", quantityToTrade));
            }
        } else if ("SELL".equals(event.direction()) && currentHolding > 0) {
            // Sell signal and we have a position
            return Optional.of(new OrderEvent(symbol, "SELL", currentHolding));
        }
        
        return Optional.empty();
    }

    @Override
    public void onFillEvent(FillEvent event) {
        if (!event.symbol().equals(symbol)) {
            return;
        }
        totalTrades++;
        
        if ("BUY".equals(event.direction())) {
            double cost = event.fillPrice() * event.quantity();
            cash -= cost;
            currentHolding += event.quantity();
            System.out.printf("FILLED: BUY %d %s @ %.2f. Cash: %.2f\n", 
                event.quantity(), symbol, event.fillPrice(), cash);

        } else if ("SELL".equals(event.direction())) {
            double proceeds = event.fillPrice() * event.quantity();
            cash += proceeds;
            currentHolding -= event.quantity();
            System.out.printf("FILLED: SELL %d %s @ %.2f. Cash: %.2f\n", 
                event.quantity(), symbol, event.fillPrice(), cash);
        }
    }

    @Override
    public void printPerformance() {
        double totalReturn = (totalValue / initialCapital) - 1;
        System.out.println("\n--- Backtest Performance ---");
        System.out.printf("Initial Capital: $%.2f\n", initialCapital);
        System.out.printf("Final Total Value: $%.2f\n", totalValue);
        System.out.printf("Total Return: %.2f%%\n", totalReturn * 100);
        System.out.printf("Total Trades: %d\n", totalTrades);
        System.out.println("--------------------------\n");
    }
}
