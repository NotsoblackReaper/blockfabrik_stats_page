package at.demski.blockfabrik_stats_page.service;

import at.demski.blockfabrik_stats_page.entities.Datapoint;
import at.demski.blockfabrik_stats_page.persistance.DatapointRepository;
import org.springframework.stereotype.Component;

import java.sql.Date;
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

    public DatabaseConnector(DatapointRepository repository) {
        this.repository = repository;
    }

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

    public List<Datapoint> getAllDesc(){
        return repository.findAllDesc();
    }
    public List<Datapoint> getAllForDayFiltered(int day){
        List<Datapoint>list=repository.findAllForDay(day);
        return list.stream().filter(distinctByKeys(Datapoint::getDatapoint_hour,Datapoint::getDatapoint_minute)).collect(Collectors.toList());
    }
    public List<Datapoint> getAllForDay(int day){
        return repository.findAllForDay(day);
    }
}
