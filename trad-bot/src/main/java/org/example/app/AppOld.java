package org.example.app;// App.java

import java.io.File;

import com.fasterxml.jackson.databind.ObjectMapper;

import org.example.app.strategy.AbstractStrategy;
import org.example.app.strategy.SimpleMovingAverageCrossover;

import io.github.cdimascio.dotenv.Dotenv;
import net.jacobpeterson.alpaca.AlpacaAPI;
import net.jacobpeterson.alpaca.model.util.apitype.BrokerAPIEndpointType;
import net.jacobpeterson.alpaca.openapi.trader.model.Account;
import picocli.CommandLine;

public class AppOld implements Runnable
{
	
	@CommandLine.Mixin
	CliOptions cliOptions = new CliOptions();
	
	public static void main(String[] args)
	{
		CommandLine.run(new AppOld(), args);
	}
	
	@Override
	public void run()
	{
		Dotenv dotenv = Dotenv.configure().ignoreIfMissing().load();
		
		String keyId = dotenv.get("APCA_API_KEY_ID");
		String secret = dotenv.get("APCA_API_SECRET_KEY");
		String baseUrl = dotenv.get("APCA_API_BASE_URL");
		
		if (keyId == null || secret == null || baseUrl == null)
		{
			System.err.println("Please set APCA_API_KEY_ID, APCA_API_SECRET_KEY, APCA_API_BASE_URL");
			System.exit(1);
		}
		
		try
		{
			// 1. Load config.json
			ObjectMapper mapper = new ObjectMapper();
			File configFile = new File("config.json");
			Config config;
			if (configFile.exists())
			{
				config = mapper.readValue(configFile, Config.class);
			}
			else
			{
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
			
			// 3. Use config
			System.out.println("Using strategy: " + config.strategy);
			System.out.println("Risk level: " + config.risk);
			System.out.println("API Base URL: " + config.apiBaseUrl);
			
			// TODO: instantiate Alpaca client and run bot with config.strategy
			// The SDK might accept base URL or infer paper vs live
			AlpacaAPI alpaca = new AlpacaAPI(keyId, secret, BrokerAPIEndpointType.SANDBOX);
			
			Account account = alpaca.trader().accounts().getAccount();  // fetch account info
			System.out.println("Account status: " + account.getStatus());
			System.out.println("Equity: " + account.getLastEquity());
			
			AbstractStrategy strategy = createStrategy(config, alpaca);
			strategy.commence();
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
	}
	
	private AbstractStrategy createStrategy(Config config, AlpacaAPI alpacaAPI)
	{
		switch (config.strategy)
		{
			case "simple-moving-average-crossover":
				return new SimpleMovingAverageCrossover(config.risk, alpacaAPI);
			default:
				throw new IllegalArgumentException("Strategy provided is invalid. Please see REDADME.md");
		}
	}
}
