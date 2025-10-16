package org.example.backtest;

import java.util.LinkedList;
import java.util.Queue;

public class BacktestEngine {

    private final DataHandler dataHandler;
    private final Strategy strategy;
    private final Portfolio portfolio;
    private final ExecutionHandler executionHandler;
    private final Queue<Event> eventQueue = new LinkedList<>();

    public BacktestEngine(DataHandler dataHandler, Strategy strategy, Portfolio portfolio, ExecutionHandler executionHandler) {
        this.dataHandler = dataHandler;
        this.strategy = strategy;
        this.portfolio = portfolio;
        this.executionHandler = executionHandler;
    }

    /**
     * Runs the backtest event loop.
     */
    public void run() {
        System.out.println("Starting backtest...");

        while (dataHandler.hasMoreEvents()) {
            // 1. Get new market data from the historical feed.
            eventQueue.add(dataHandler.getNextEvent());

            while (!eventQueue.isEmpty()) {
                Event event = eventQueue.poll();

                if (event instanceof MarketEvent marketEvent) {
                    // 2. Update portfolio value based on the new market price.
                    portfolio.onMarketEvent(marketEvent);

                    // 3. Allow the strategy to generate a signal based on the new data.
                    strategy.generateSignal(marketEvent)
                            .ifPresent(eventQueue::add);

                } else if (event instanceof SignalEvent signalEvent) {
                    // 4. The portfolio processes the signal to create an order.
                    portfolio.onSignalEvent(signalEvent)
                            .ifPresent(eventQueue::add);

                } else if (event instanceof OrderEvent orderEvent) {
                    // 5. The execution handler simulates filling the order.
                    executionHandler.onOrderEvent(orderEvent)
                            .ifPresent(eventQueue::add);

                } else if (event instanceof FillEvent fillEvent) {
                    // 6. The portfolio updates its holdings based on the executed trade.
                    portfolio.onFillEvent(fillEvent);
                }
            }
        }
        System.out.println("Backtest finished.");
        // After the loop, print the final performance metrics.
        portfolio.printPerformance();
    }
}
