package com.tradingbot.backtesting;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;

@RestController
@RequestMapping("/backtest")
public class BacktestController {

    @Autowired
    private BacktestingService backtestingService;

    @PostMapping("/sma")
    public BacktestResult runSmaBacktest(
            @RequestParam String symbol,
            @RequestParam String start, // e.g., 2022-01-01T10:00:00Z
            @RequestParam String end) {
        
        DateTimeFormatter formatter = DateTimeFormatter.ISO_OFFSET_DATE_TIME;
        ZonedDateTime startDate = ZonedDateTime.parse(start, formatter);
        ZonedDateTime endDate = ZonedDateTime.parse(end, formatter);

        return backtestingService.run(symbol, startDate, endDate);
    }
}