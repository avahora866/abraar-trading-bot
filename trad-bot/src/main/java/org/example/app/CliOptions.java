package org.example.app;// CliOptions.java
import picocli.CommandLine.Option;

public class CliOptions {
	@CommandLine.Option(names = "--strategy", description = "The trading strategy to use.")
	public String strategy;
	
	@CommandLine.Option(names = "--risk", description = "The risk level (e.g., low, medium, high).")
	public String risk;
	
	// New options for backtesting
	@CommandLine.Option(names = "--backtest", description = "Run in backtesting mode.")
	public boolean backtestMode = false;
	
	@CommandLine.Option(names = "--start-date", description = "Start date for backtesting (YYYY-MM-DD). Required for backtest mode.")
	public String startDate;
	
	@CommandLine.Option(names = "--end-date", description = "End date for backtesting (YYYY-MM-DD). Required for backtest mode.")
	public String endDate;
	
	@CommandLine.Option(names = "--symbol", description = "The stock symbol to backtest (e.g., SPY). Required for backtest mode.")
	public String symbol;
}
