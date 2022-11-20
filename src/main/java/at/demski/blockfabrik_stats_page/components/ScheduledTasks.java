package at.demski.blockfabrik_stats_page.components;

import at.demski.blockfabrik_stats_page.BlockfabrikStatsPageApplication;
import at.demski.blockfabrik_stats_page.service.API.DatabaseConnector;
import at.demski.blockfabrik_stats_page.entities.VisitorCount;
import at.demski.blockfabrik_stats_page.service.API.BoulderadoAPI;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.text.SimpleDateFormat;

@Component
public class ScheduledTasks {
    @Autowired
    DatabaseConnector dbConnector;

    private static final SimpleDateFormat dateFormat = new SimpleDateFormat("HH:mm:ss");

    @Scheduled(cron="0 */5 7-21 * * *")
    @ConditionalOnProperty(value = "BlockfabrikStatsPageApplication.data_scraping",matchIfMissing = false,havingValue = "true")
    public void reportCurrentCount() throws IOException {
        if(!BlockfabrikStatsPageApplication.data_scraping)return;
        VisitorCount count= BoulderadoAPI.GetValue();
        dbConnector.addCurrentData(count);
    }

    @Scheduled(cron="0 0 7 * * *")
    @ConditionalOnProperty(value = "BlockfabrikStatsPageApplication.data_scraping",matchIfMissing = false,havingValue = "true")
    public void updatePastDay() throws IOException {

    }
}
