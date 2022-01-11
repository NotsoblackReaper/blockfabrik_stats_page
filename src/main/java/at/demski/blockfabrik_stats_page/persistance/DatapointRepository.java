package at.demski.blockfabrik_stats_page.persistance;

import at.demski.blockfabrik_stats_page.entities.Datapoint;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface DatapointRepository extends CrudRepository<Datapoint,Integer> {
    List<Datapoint> findAll();

    @Query(value = "select * from datapoint order by datapoint_date desc, datapoint_hour desc, datapoint_minute desc", nativeQuery = true)
    List<Datapoint> findAllDesc();
}
