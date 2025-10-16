package org.example.backtest;

import java.util.Optional;

/**
 * Interface for the (simulated) execution handler.
 */
public interface ExecutionHandler {
	Optional<FillEvent> onOrderEvent(OrderEvent event);
}