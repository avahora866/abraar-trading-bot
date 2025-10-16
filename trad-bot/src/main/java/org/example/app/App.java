package org.example.app;

import java.io.File;

import com.fasterxml.jackson.databind.ObjectMapper;

import org.example.app.strategy.AbstractStrategy;
import org.example.backtest.AlpacaDataHandler;
import org.example.backtest.BacktestEngine;
import org.example.backtest.BasicPortfolio;
import org.example.backtest.DataHandler;
import org.example.backtest.ExecutionHandler;
import org.example.backtest.SimulatedExecutionHandler;
import org.example.backtest.SmaCrossoverBacktestStrategy;
import org.example.backtest.Strategy;

import io.github.cdimascio.dotenv.Dotenv;
import net.jacobpeterson.alpaca.AlpacaAPI;
import net.jacobpeterson.alpaca.openapi.broker.model.Portfolio;
import picocli.CommandLine;

public class App implements Runnable {

    @CommandLine.Mixin
    CliOptions cliOptions = new CliOptions();

    public static void main(String[] args) {
        CommandLine.run(new App(), args);
    }

    @Override
    public void run() {
        Dotenv dotenv = Dotenv.configure().ignoreIfMissing().load();

        String keyId = dotenv.get("APCA_API_KEY_ID");
        String secret = dotenv.get("APCA_API_SECRET_KEY");

        if (keyId == null || secret == null) {
            System.err.println("Please set APCA_API_KEY_ID and APCA_API_SECRET_KEY in your .env file.");
            System.exit(1);
        }

        try {
            // 1. Load config.json or use defaults
            ObjectMapper mapper = new ObjectMapper();
            File configFile = new File("config.json");
            Config config;
            if (configFile.exists()) {
                config = mapper.readValue(configFile, Config.class);
            } else {
                config = new Config(); // defaults
            }

            // 2. Override with CLI arguments if provided
			if (cliOptions.strategy != null)
			{
				config.strategy = cliOptions.strategy;
			}
			if (cliOptions.risk != null)
			{
				config.risk = cliOptions.risk;
			}

            AlpacaAPI alpacaAPI = new AlpacaAPI(keyId, secret, BrokerAPIEndpointType.SANDBOX);

            if (cliOptions.backtestMode) {
                // --- RUN IN BACKTEST MODE ---
                validateBacktestOptions();
                System.out.println("Running in BACKTESTING mode...");
                runBacktest(config, alpacaAPI);
            } else {
                // --- RUN IN LIVE TRADING MODE ---
                System.out.println("Running in LIVE TRADING mode...");
                System.out.println("Account Status: " + alpacaAPI.trader().accounts().getAccount().getStatus());
                AbstractStrategy strategy = createLiveStrategy(config, alpacaAPI);
                strategy.commence();
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    /**
     * Instantiates and runs the backtesting engine.
     */
    private void runBacktest(Config config, AlpacaAPI alpacaAPI) {
        String symbol = cliOptions.symbol;
        
        // 1. Instantiate backtesting components
        DataHandler dataHandler = new AlpacaDataHandler(alpacaAPI, symbol, cliOptions.startDate, cliOptions.endDate);
        Portfolio portfolio = new BasicPortfolio(symbol, 100_000.0); // $100k initial capital
        ExecutionHandler executionHandler = new SimulatedExecutionHandler();
        
        // For now, we only have one backtestable strategy
        Strategy backtestStrategy = new SmaCrossoverBacktestStrategy(symbol, 10, 50); // Using 10/50 day SMA

        // 2. Create and run the engine
        BacktestEngine engine = new BacktestEngine(dataHandler, backtestStrategy, portfolio, executionHandler);
        engine.run();
    }

    /**
     * Creates a strategy instance for live trading.
     */
    private AbstractStrategy createLiveStrategy(Config config, AlpacaAPI alpacaAPI) {
        switch (config.strategy) {
            case "simple-moving-average-crossover":
                return new SimpleMovingAverageCrossover(config.risk, alpacaAPI);
            default:
                throw new IllegalArgumentException("Strategy provided is invalid. Please see README.md");
        }
    }
    
    /**
     * Validates that all required command line options for backtesting are present.
     */
    private void validateBacktestOptions() {
        if (cliOptions.symbol == null || cliOptions.startDate == null || cliOptions.endDate == null) {
            throw new IllegalArgumentException("For backtesting, you must provide --symbol, --start-date, and --end-date.");
        }
    }
}
