package org.example.backtest;

/**
 * Interface for a data source. In backtesting, this feeds historical data.
 */
public interface DataHandler {
	boolean hasMoreEvents();
	MarketEvent getNextEvent();
}