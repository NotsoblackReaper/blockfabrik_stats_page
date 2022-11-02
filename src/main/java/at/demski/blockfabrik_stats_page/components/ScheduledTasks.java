package at.demski.blockfabrik_stats_page.components;

import at.demski.blockfabrik_stats_page.BlockfabrikStatsPageApplication;
import at.demski.blockfabrik_stats_page.service.DatabaseConnector;
import at.demski.blockfabrik_stats_page.service.VisitorCount;
import at.demski.blockfabrik_stats_page.service.VisitorCountManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Conditional;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.sql.Date;
import java.sql.Time;
import java.text.SimpleDateFormat;

@Component
public class ScheduledTasks {
    @Autowired
    DatabaseConnector dbConnector;

    private static final SimpleDateFormat dateFormat = new SimpleDateFormat("HH:mm:ss");

    @Scheduled(cron="0 */5 7-21 * * *")
    @ConditionalOnProperty(value = "BlockfabrikStatsPageApplication.data_scraping",matchIfMissing = false,havingValue = "true")
    public void reportCurrentCount() throws IOException {
        System.out.println("Trying to add datapoint");
        if(!BlockfabrikStatsPageApplication.data_scraping)return;
        VisitorCount count=VisitorCountManager.GetValue();
        System.out.println("Trying to add datapoint");
        dbConnector.addCurrentData(count);
    }

    @Scheduled(cron="0 0 7 * * *")
    @ConditionalOnProperty(value = "BlockfabrikStatsPageApplication.data_scraping",matchIfMissing = false,havingValue = "true")
    public void updatePastDay() throws IOException {

    }

    @Scheduled(cron="0/10 * * * * *")
    public void heartbeat() {
        System.out.println("Running!");
    }
}
