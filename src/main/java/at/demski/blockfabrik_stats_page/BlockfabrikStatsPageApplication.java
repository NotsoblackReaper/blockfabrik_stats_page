package at.demski.blockfabrik_stats_page;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.WebApplicationType;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class BlockfabrikStatsPageApplication {

    public static boolean data_scraping = false;

    public static void main(String[] args) {
        boolean web_server = true;
        String scrape_str = System.getenv("DATA_SCRAPING");
        if (scrape_str != null)
            data_scraping = Boolean.parseBoolean(scrape_str);
        String webSv_str = System.getenv("WEB_SERVER");
        if (webSv_str != null)
            web_server = Boolean.parseBoolean(webSv_str);

        System.out.println("---------------------------------------------");
        System.out.println("Starting with Configuration:");
        System.out.println("Data Scraping: "+data_scraping);
        System.out.println("Web Server:    "+web_server);
        System.out.println("---------------------------------------------");

        if (web_server)
            SpringApplication.run(BlockfabrikStatsPageApplication.class, args);
        else
            new SpringApplicationBuilder(BlockfabrikStatsPageApplication.class)
                    .web(WebApplicationType.NONE)
                    .run(args);
    }

}
