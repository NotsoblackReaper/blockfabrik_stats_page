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

    @Query(value="select * from day_data where date=?1",nativeQuery = true)
    DayData getDaybyDate(Date date);

    DayData save(DayData dayData);

    @Query(value="select * from day_data order by date desc limit ?1",nativeQuery = true)
    List<DayData>getAllDesc(int limit);
}
