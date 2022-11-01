package at.demski.blockfabrik_stats_page.service;

import at.demski.blockfabrik_stats_page.entities.BlockfabrikDatapoint;
import at.demski.blockfabrik_stats_page.entities.Datapoint;
import at.demski.blockfabrik_stats_page.entities.DayData;
import at.demski.blockfabrik_stats_page.entities.WeatherData;
import at.demski.blockfabrik_stats_page.persistance.BlockfabrikDatapointRepository;
import at.demski.blockfabrik_stats_page.persistance.DatapointRepository;
import at.demski.blockfabrik_stats_page.persistance.DayDataRepository;
import at.demski.blockfabrik_stats_page.service.utils.DateManager;
import org.springframework.stereotype.Component;

import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Time;
import java.util.Arrays;
import java.util.Calendar;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;

@Component
public class DatabaseConnector {

    private static <T> Predicate<T> distinctByKeys(Function<? super T, ?>... keyExtractors)
    {
        final Map<List<?>, Boolean> seen = new ConcurrentHashMap<>();

        return t ->
        {
            final List<?> keys = Arrays.stream(keyExtractors)
                    .map(ke -> ke.apply(t))
                    .collect(Collectors.toList());

            return seen.putIfAbsent(keys, Boolean.TRUE) == null;
        };
    }

    final DatapointRepository repository;
    final BlockfabrikDatapointRepository datapointRepository;
    final DayDataRepository dayRepository;

    public DatabaseConnector(DatapointRepository repository, BlockfabrikDatapointRepository datapointRepository,DayDataRepository dayRepository) {
        System.out.println("Creating DB Connector");
        this.repository = repository;
        this.datapointRepository=datapointRepository;
        this.dayRepository=dayRepository;
        System.out.println("Created DB Connector");
    }

    /***
     * @deprecated replaced by {@link #addCurrentData(VisitorCount)}
     */
    @Deprecated
    public void insertDatapoint(Date date, Time time, VisitorCount count){
        Datapoint data=new Datapoint();

        data.setDatapoint_id(null);
        Calendar c=Calendar.getInstance();
        c.setTime(date);
        data.setDatapoint_date(date);
        data.setDatapoint_day(c.get(Calendar.DAY_OF_WEEK));
        c.setTime(time);
        data.setDatapoint_hour(c.get(Calendar.HOUR_OF_DAY));
        data.setDatapoint_minute(c.get(Calendar.MINUTE));

        if(data.getDatapoint_hour()<=7&&data.getDatapoint_minute()<30)
            return;
        if(data.getDatapoint_hour()>=22)
            return;
        data.setDatapoint_act(count.getCounter());
        data.setDatapoint_max(count.getMaxcount());

        repository.save(data);
    }

    public int addBlankDay(){
        WeatherData current=WeatherAPI.getCurrentWeather();
        DayData data=new DayData(null,DateManager.today(),current.temperature,current.downpour,current.wind,false,null);
        DayData inserted=dayRepository.saveDayData(data);
        return inserted.getDay_id();
    }

    /***
     * Insert a new Datapoint into the "blockfabrik_datapoint" table
     * Creates a new "day_data" entry if needed
     * @param count  Current visitor count
     */
    public void addCurrentData(VisitorCount count){
        Integer dayId=dayRepository.getIdForDay(DateManager.today());
        int id=dayId==null?addBlankDay():dayId;

        BlockfabrikDatapoint datapoint=new BlockfabrikDatapoint(null,id,DateManager.hour(),DateManager.minute(),count.getCounter());

        /*
        if(datapoint.getHour()<=7&&datapoint.getMinute()<30)
            return;
        if(datapoint.getHour()>=22)
            return;
        */

        datapointRepository.save(datapoint);
    }

    public List<Datapoint> getAllDesc(){
        return repository.findAllDesc();
    }
    public List<Datapoint> getAllForDayFiltered(int day){
        List<Datapoint>list=repository.findAllForDay(day);
        return list.stream().filter(distinctByKeys(Datapoint::getDatapoint_hour,Datapoint::getDatapoint_minute)).collect(Collectors.toList());
    }

    public List<Datapoint> getAllDesc(int limit){
        return repository.findAllDesc();
    }

    public List<Datapoint> getAllForDay(int day){
        return repository.findAllForDay(day);
    }
}
