package at.demski.blockfabrik_stats_page.persistance;

import at.demski.blockfabrik_stats_page.entities.BlockfabrikDatapoint;
import org.springframework.data.repository.CrudRepository;

public interface BlockfabrikDatapointRepository extends CrudRepository<BlockfabrikDatapoint,Integer> {
}
