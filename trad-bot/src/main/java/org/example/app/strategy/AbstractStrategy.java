/*
 * $HeadURL$
 * 
 * (c) 2025 IIZUKA Software Technologies Ltd.  All rights reserved.
 */
package org.example.app.strategy;

import java.util.List;

import org.example.app.utility.AlpacaClient;
import org.example.app.utility.StockFilter;
import org.example.app.utility.StockInformation;

import net.jacobpeterson.alpaca.AlpacaAPI;
import net.jacobpeterson.alpaca.openapi.broker.model.Asset;

/**
 *
 *
 * @author Abraar Vahora
 * @version $Id$
 */
public abstract class AbstractStrategy
{
	protected String risk;
	protected AlpacaClient alpacaClient;
	protected StockFilter stockFilter;
	protected StockInformation stockInformation;
	protected List<Asset> assetsList;
	
	public AbstractStrategy(String risk, AlpacaAPI alpacaAPI)
	{
		this.risk = risk;
		this.alpacaClient = new AlpacaClient(alpacaAPI);
		this.stockFilter = new StockFilter(alpacaAPI);
		this.stockInformation = new StockInformation(alpacaAPI);
	}
	
	public abstract void commence();
}
