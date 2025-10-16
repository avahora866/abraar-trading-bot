package org.example.backtest;

import java.util.Optional;

/**
 * Interface for a trading strategy.
 */
public interface Strategy {
	Optional<SignalEvent> generateSignal(MarketEvent event);
}