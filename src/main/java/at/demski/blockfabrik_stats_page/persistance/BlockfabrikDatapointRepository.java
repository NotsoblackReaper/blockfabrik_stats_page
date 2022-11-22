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
}
