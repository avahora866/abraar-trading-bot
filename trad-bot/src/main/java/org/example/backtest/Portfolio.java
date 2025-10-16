package org.example.backtest;

import java.util.Optional;

/**
 * Interface for the portfolio manager. Tracks cash, positions, and performance.
 */
interface Portfolio {
	void onMarketEvent(MarketEvent event);
	Optional<OrderEvent> onSignalEvent(SignalEvent event);
	void onFillEvent(FillEvent event);
	void printPerformance();
}