package at.demski.blockfabrik_stats_page.persistance;

import at.demski.blockfabrik_stats_page.entities.Datapoint;
import at.demski.blockfabrik_stats_page.entities.DayData;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import java.sql.Date;
import java.util.List;

public interface DayDataRepository extends CrudRepository<DayData,Integer> {
    @Query(value="select day_id from day_data where date=?1",nativeQuery = true)
    Integer getIdForDay(Date date);

    DayData save(DayData dayData);
}
