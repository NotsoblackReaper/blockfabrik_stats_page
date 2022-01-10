package at.demski.blockfabrik_stats_page;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication(exclude = {org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration.class})
@EnableScheduling
public class BlockfabrikStatsPageApplication {

    public static void main(String[] args) {
        SpringApplication.run(BlockfabrikStatsPageApplication.class, args);
    }

}