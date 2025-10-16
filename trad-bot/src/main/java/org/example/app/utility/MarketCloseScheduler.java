package org.example.app.utility;

import java.time.Duration;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class MarketCloseScheduler {

    private final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);

    public void scheduleAtMarketClose(Runnable task) {
        ZonedDateTime now = ZonedDateTime.now(ZoneId.of("America/New_York"));
        ZonedDateTime nextClose = now.withHour(16).withMinute(0).withSecond(0).withNano(0);

        if (now.compareTo(nextClose) > 0) {
            // Already past today's close, schedule for tomorrow
            nextClose = nextClose.plusDays(1);
        }

        long delay = Duration.between(now, nextClose).toMillis();

        scheduler.schedule(() -> {
            task.run();
            // Reschedule for next day
            scheduleAtMarketClose(task);
        }, delay, TimeUnit.MILLISECONDS);
    }
	
	public void clear()
	{
		try
		{
			scheduler.awaitTermination(2, TimeUnit.MINUTES);
		}
		catch (InterruptedException e)
		{
			throw new RuntimeException("Failed awaiting termination of scheduler");
		}
	}
}
