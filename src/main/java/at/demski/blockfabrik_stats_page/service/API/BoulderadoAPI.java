package at.demski.blockfabrik_stats_page.service.API;
import at.demski.blockfabrik_stats_page.entities.VisitorCount;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.net.URL;

public class BoulderadoAPI {
    public static VisitorCount GetValue() throws IOException {
        URL url = new URL("https://www.boulderado.de/boulderadoweb/gym-clientcounter/index.php?mode=get&token=eyJhbGciOiJIUzI1NiIsICJ0eXAiOiJKV1QifQ.eyJjdXN0b21lciI6IkJsb2NrZmFicmlrV2llbiJ9.yymz1Eg_-jX28iMdaq1aGVb0iD4-29uWVkuxZd7a_9U&raw=1");
        ObjectMapper mapper=new ObjectMapper();
        return mapper.readValue(url,VisitorCount.class);
    }
}
