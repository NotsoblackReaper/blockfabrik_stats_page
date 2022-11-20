package at.demski.blockfabrik_stats_page;

import at.demski.blockfabrik_stats_page.service.utils.tensorflow.ModelHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.WebApplicationType;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class BlockfabrikStatsPageApplication {
    private static Logger logger = LoggerFactory.getLogger(BlockfabrikStatsPageApplication.class);

    public static boolean data_scraping = false;
    public static boolean tf_support=false;

    public static void main(String[] args) {
        boolean web_server = true;
        String tf_str = System.getenv("TF_SUPPORT");
        if (tf_str != null)
            tf_support = Boolean.parseBoolean(tf_str);
        String scrape_str = System.getenv("DATA_SCRAPING");
        if (scrape_str != null)
            data_scraping = Boolean.parseBoolean(scrape_str);
        String webSv_str = System.getenv("WEB_SERVER");
        if (webSv_str != null)
            web_server = Boolean.parseBoolean(webSv_str);

        logger.info("\n---------------------------------------------\n"+
                "BlockfabrikStatsPageApplication version 0.2\n"+
                "Starting with Configuration:\n"+
                "Data Scraping: "+data_scraping+(scrape_str==null?"(default)\n":"\n")+
                "Web Server:    "+web_server+(webSv_str==null?"(default)\n":"\n")+
                "Tensorflow:    "+tf_support+(tf_str==null?"(default)\n":"\n")+
                "---------------------------------------------");

        if (web_server)
            SpringApplication.run(BlockfabrikStatsPageApplication.class, args);
        else
            new SpringApplicationBuilder(BlockfabrikStatsPageApplication.class)
                    .web(WebApplicationType.NONE)
                    .run(args);
    }
}
