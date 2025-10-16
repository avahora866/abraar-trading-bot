package org.example.backtest;

import java.util.Optional;

import net.jacobpeterson.alpaca.openapi.marketdata.model.StockBar;

// Marker interface for all event types
interface Event {}

// Event representing a single bar of market data (e.g., one day's OHLCV)
record MarketEvent(String symbol, StockBar data) implements Event {}

// Event representing a trading signal from the strategy
record SignalEvent(String symbol, String direction, double price) implements Event {} // direction = "BUY" or "SELL"

// Event representing an order to be sent to the (simulated) broker
record OrderEvent(String symbol, String direction, int quantity) implements Event {}

// Event representing a successfully executed order
record FillEvent(String symbol, String direction, int quantity, double fillPrice) implements Event {}







