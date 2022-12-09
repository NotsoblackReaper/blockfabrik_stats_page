package at.demski.blockfabrik_stats_page.persistance;

import at.demski.blockfabrik_stats_page.entities.BlockfabrikDatapoint;
import at.demski.blockfabrik_stats_page.entities.Datapoint;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface DatapointRepository extends CrudRepository<Datapoint,Integer> {
    List<Datapoint> findAll();

    @Query(value = "select * from datapoint order by datapoint_date desc, datapoint_hour desc, datapoint_minute desc", nativeQuery = true)
    List<Datapoint> findAllDesc();

    @Query(value = "select * from datapoint order by datapoint_date desc, datapoint_hour desc, datapoint_minute desc limit ?1", nativeQuery = true)
    List<Datapoint> findAllDesc(int limit);

    @Query(value="select * from datapoint where datapoint_day=?1 order by datapoint_hour asc, datapoint_minute asc",nativeQuery = true)
    List<Datapoint> findAllForDay(int day);

    @Query(value="SELECT datapoint_id,ROUND(AVG(datapoint_act)) as datapoint_act,datapoint_max,datapoint_date,datapoint_day,datapoint_hour,datapoint_minute,id " +
            "FROM datapoint where datapoint_day=?1 " +
            "GROUP BY datapoint_day, datapoint_hour, FLOOR(datapoint_minute / ?2) " +
            "order by datapoint_hour asc, datapoint_minute asc",nativeQuery = true)
    List<Datapoint> getAverageForDay(int day, int averageLength);
}
