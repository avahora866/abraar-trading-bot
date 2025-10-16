package org.example.backtest;

import java.time.Duration;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.Optional;

import net.jacobpeterson.alpaca.openapi.marketdata.model.StockBar;

public class SmaCrossoverBacktestStrategy implements Strategy {

    private final String symbol;
    private final BarSeries series;
    private final SMAIndicator shortSma;
    private final SMAIndicator longSma;
    private final int longTermPeriod;

    public SmaCrossoverBacktestStrategy(String symbol, int shortTermPeriod, int longTermPeriod) {
        this.symbol = symbol;
        this.longTermPeriod = longTermPeriod;
        this.series = new BaseBarSeriesBuilder().withName(symbol).build();
        ClosePriceIndicator closePrice = new ClosePriceIndicator(series);
        this.shortSma = new SMAIndicator(closePrice, shortTermPeriod);
        this.longSma = new SMAIndicator(closePrice, longTermPeriod);
    }

    @Override
    public Optional<SignalEvent> generateSignal(MarketEvent event) {
        addBarToSeries(event.data());

        // We need enough data to calculate the long SMA before we can trade
        if (series.getBarCount() <= longTermPeriod) {
            return Optional.empty();
        }

        int currentIndex = series.getEndIndex();
        int prevIndex = currentIndex - 1;

        Num prevShortSma = shortSma.getValue(prevIndex);
        Num prevLongSma = longSma.getValue(prevIndex);
        Num currentShortSma = shortSma.getValue(currentIndex);
        Num currentLongSma = longSma.getValue(currentIndex);

        double currentPrice = event.data().getClose();

        // Bullish crossover: short SMA crosses above long SMA
        if (prevShortSma.isLessThanOrEqual(prevLongSma) && currentShortSma.isGreaterThan(currentLongSma)) {
            return Optional.of(new SignalEvent(symbol, "BUY", currentPrice));
        }

        // Bearish crossover: short SMA crosses below long SMA
        if (prevShortSma.isGreaterThanOrEqual(prevLongSma) && currentShortSma.isLessThan(currentLongSma)) {
            return Optional.of(new SignalEvent(symbol, "SELL", currentPrice));
        }

        return Optional.empty();
    }

    // Helper to convert from Alpaca Bar to ta4j Bar
    private void addBarToSeries(StockBar alpacaBar) {
        ZonedDateTime endTime = ZonedDateTime.ofInstant(alpacaBar.getTimestamp(), ZoneId.systemDefault());
        series.addBar(
                Duration.ofDays(1), // Assuming daily bars
                endTime,
                alpacaBar.getOpen(),
                alpacaBar.getHigh(),
                alpacaBar.getLow(),
                alpacaBar.getClose(),
                alpacaBar.getVolume());
    }
}
