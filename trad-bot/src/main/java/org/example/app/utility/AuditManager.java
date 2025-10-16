/*
 * $HeadURL$
 * 
 * (c) 2025 IIZUKA Software Technologies Ltd.  All rights reserved.
 */
package org.example.app.utility;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.HashMap;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 *
 *
 * @author Abraar Vahora
 * @version $Id$
 */
public class AuditManager
{
	private static final Logger auditLogger = LogManager.getLogger("AuditLogger");
	
	public static void buy(String symbol, int quantity, BigDecimal price)
	{
		HashMap<String, String> map = new HashMap<>();
		map.put("timestamp", Instant.now().toString());
		map.put("symbol", symbol);
		map.put("event", "BUY");
		map.put("quantity", String.valueOf(quantity));
		map.put("price", String.valueOf(price));
		auditLogger.info(map);
	}
	
	public static void sell(String symbol, int quantity, String price)
	{
		HashMap<String, String> map = new HashMap<>();
		map.put("timestamp", Instant.now().toString());
		map.put("symbol", symbol);
		map.put("event", "SELL");
		map.put("quantity", String.valueOf(quantity));
		map.put("price", String.valueOf(price));
		auditLogger.info(map);
	}
	
	public static void event(String event)
	{
		HashMap<String, String> map = new HashMap<>();
		map.put("timestamp", Instant.now().toString());
		map.put("event", event);
		auditLogger.info(map);
	}
	
	public static void message(String event, String message)
	{
		HashMap<String, String> map = new HashMap<>();
		map.put("timestamp", Instant.now().toString());
		map.put("event", event);
		map.put("message", message);
		auditLogger.info(map);
	}
}
