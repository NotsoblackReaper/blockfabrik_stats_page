package at.demski.blockfabrik_stats_page.persistance;

import at.demski.blockfabrik_stats_page.entities.Datapoint;
import org.springframework.data.repository.CrudRepository;

public interface DatapointRepository extends CrudRepository<Datapoint,Integer> {
}
