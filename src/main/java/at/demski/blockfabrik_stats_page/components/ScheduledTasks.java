package at.demski.blockfabrik_stats_page.components;

import at.demski.blockfabrik_stats_page.service.DatabaseConnector;
import at.demski.blockfabrik_stats_page.service.VisitorCount;
import at.demski.blockfabrik_stats_page.service.VisitorCountManager;
import org.springframework.beans.factory.annotation.Autowired;
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

    @Scheduled(cron="0 */2 7-21 * * *")
    public void reportCurrentCount() throws IOException {
        VisitorCount count=VisitorCountManager.GetValue();
        java.util.Date utilDate=new java.util.Date();
        //System.out.println("Current Time: "+dateFormat.format(utilDate)+" Current Visitor count: "+count);
        dbConnector.insertDatapoint(new Date(utilDate.getTime()),new Time(utilDate.getTime()),count);
    }
}
