package at.demski.blockfabrik_stats_page;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.WebApplicationType;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication(exclude = {org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration.class})
@EnableScheduling
public class BlockfabrikStatsPageApplication {

    public static boolean data_scraping;

    public static void main(String[] args) {
        data_scraping = Boolean.parseBoolean(args[0]);
        boolean web_server = Boolean.parseBoolean(args[1]);
        System.out.println("Configuration:\nData Scraping:\t"+data_scraping+"\nWeb Server:\t"+web_server);
        if (web_server)
            SpringApplication.run(BlockfabrikStatsPageApplication.class, args);
        else
            new SpringApplicationBuilder(BlockfabrikStatsPageApplication.class)
                    .web(WebApplicationType.NONE)
                    .run(args);
    }

}
