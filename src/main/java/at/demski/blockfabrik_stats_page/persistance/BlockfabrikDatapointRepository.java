package at.demski.blockfabrik_stats_page.persistance;

import at.demski.blockfabrik_stats_page.entities.BlockfabrikDatapoint;
import at.demski.blockfabrik_stats_page.entities.Datapoint;
import at.demski.blockfabrik_stats_page.entities.DayData;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import java.sql.Date;
import java.util.List;

public interface BlockfabrikDatapointRepository extends CrudRepository<BlockfabrikDatapoint,Integer> {

    BlockfabrikDatapoint save(BlockfabrikDatapoint datapoint);

    @Query(value = "select * from blockfabrik_datapoint join day_data dd on dd.day_id = blockfabrik_datapoint.day_id " +
            "where dd.date=?1 and (hour<?2 or (hour=?2 and minute<=?3)) order by hour desc, minute desc", nativeQuery = true)
    List<BlockfabrikDatapoint> getBeforeTime(Date date, int hour, int minute);

    @Query(value = "select * from blockfabrik_datapoint join day_data dd on dd.day_id = blockfabrik_datapoint.day_id " +
            "where dd.date=?1 order by hour desc, minute desc", nativeQuery = true)
    List<BlockfabrikDatapoint> getAllByDate(Date date);

    @Query(value="SELECT blockfabrik_datapoint.hour,blockfabrik_datapoint.minute, ROUND(AVG(blockfabrik_datapoint.value))\n" +
            "FROM blockfabrik_datapoint" +
            "         INNER JOIN day_data ON blockfabrik_datapoint.day_id = day_data.day_id where day_data.weekday=?1 " +
            "GROUP BY day_data.weekday, blockfabrik_datapoint.hour, FLOOR(blockfabrik_datapoint.minute / ?2) " +
            "order by day_data.date desc, hour asc, minute asc",nativeQuery = true)
    List<BlockfabrikDatapoint> getAverageForDay(int day,int averageLength);
}
