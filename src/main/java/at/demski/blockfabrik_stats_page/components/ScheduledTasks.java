package at.demski.blockfabrik_stats_page.components;

import at.demski.blockfabrik_stats_page.service.VisitorCount;
import at.demski.blockfabrik_stats_page.service.VisitorCountManager;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

@Component
public class ScheduledTasks {
    private static final SimpleDateFormat dateFormat = new SimpleDateFormat("HH:mm:ss");

    @Scheduled(cron="0 */2 7-22 * * *")
    public void reportCurrentCount() throws IOException {
        VisitorCount count=VisitorCountManager.GetValue();
        System.out.println("Current Time: "+dateFormat.format(new Date())+" Current Visitor count: "+count);
    }
}
