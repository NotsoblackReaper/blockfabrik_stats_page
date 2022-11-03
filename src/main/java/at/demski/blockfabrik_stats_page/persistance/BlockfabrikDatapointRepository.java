package at.demski.blockfabrik_stats_page.persistance;

import at.demski.blockfabrik_stats_page.entities.BlockfabrikDatapoint;
import at.demski.blockfabrik_stats_page.entities.DayData;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

public interface BlockfabrikDatapointRepository extends CrudRepository<BlockfabrikDatapoint,Integer> {

}
