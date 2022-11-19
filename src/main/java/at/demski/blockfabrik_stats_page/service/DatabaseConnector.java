package at.demski.blockfabrik_stats_page.service;

import at.demski.blockfabrik_stats_page.entities.BlockfabrikDatapoint;
import at.demski.blockfabrik_stats_page.entities.Datapoint;
import at.demski.blockfabrik_stats_page.entities.DayData;
import at.demski.blockfabrik_stats_page.entities.WeatherData;
import at.demski.blockfabrik_stats_page.persistance.BlockfabrikDatapointRepository;
import at.demski.blockfabrik_stats_page.persistance.DatapointRepository;
import at.demski.blockfabrik_stats_page.persistance.DayDataRepository;
import at.demski.blockfabrik_stats_page.service.utils.DatapointUtils;
import at.demski.blockfabrik_stats_page.service.utils.DateManager;
import org.springframework.stereotype.Component;

import java.sql.Date;
import java.sql.Time;
import java.util.*;
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

    public DayData addBlankDay(){
        WeatherData current=WeatherAPI.getCurrentWeather();
        DayData data=new DayData(DateManager.today(),current.temperature,current.downpour,current.wind,false,null,false);
        DayData inserted=dayRepository.save(data);
        System.out.println("Adding new Day with id "+inserted);
        return inserted;
    }

    /***
     * Insert a new Datapoint into the "blockfabrik_datapoint" table
     * Creates a new "day_data" entry if needed
     * @param count  Current visitor count
     */
    public void addCurrentData(VisitorCount count){
        DayData day=dayRepository.getDaybyDate(DateManager.today());
        System.out.println(day);
        if(day==null)day=addBlankDay();
        BlockfabrikDatapoint datapoint=
                new BlockfabrikDatapoint(null,day,
                        DateManager.hour(),DateManager.minute(),count.getCounter());

        if(datapoint.getHour()<=7&&datapoint.getMinute()<30)
            return;
        if(datapoint.getHour()>=22)
            return;

        System.out.println("Inserting Datapoint:\n"+datapoint);
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

    public List<DayData>getDayDataDesc(int limit){return dayRepository.getAllDesc(limit);}

    private float getMedian(List<Datapoint> datapointList, final int time) {
        List<Integer> data = datapointList.stream().filter(d -> d.getDatapoint_hour() * 60 + d.getDatapoint_minute() == time)
                .map(Datapoint::getDatapoint_act).sorted().collect(Collectors.toList());
        return (float) data.stream().mapToInt(value -> value).average().orElse(Double.NaN);
    }

    public List<Datapoint> getHalfHourAverages(int day){
        List<Datapoint> rawlist;
        List<Datapoint> medianList = new ArrayList<>();

        rawlist = getAllForDay(day);

        for (int j = 7 * 60 + 30; j < 22 * 60; j += 2) {
            float median = getMedian(rawlist, j);
            medianList.add(new Datapoint(Math.round(median), j / 60, j % 60, day));
        }

        List<Datapoint> points = new ArrayList<>();
        float sum = 0;
        int n = 0;
        for (int j = 0; j < medianList.size(); ++j) {
            Datapoint p = medianList.get(j);
            sum += p.getDatapoint_act();
            ++n;
            if (p.getDatapoint_minute() == 28 || p.getDatapoint_minute() == 58) {
                points.add(new Datapoint(
                        Math.round(sum / n),
                        p.getDatapoint_hour() + (p.getDatapoint_minute() == 28 ? 0 : 1),
                        p.getDatapoint_minute() == 28 ? 30 : 0,
                        p.getDatapoint_day()));
                sum = 0;
                n = 0;
            }
        }

        return points;
    }

    public List<BlockfabrikDatapoint> getHalfHourAveragesNew(int day,int averageLength){
        List<DayData> dayData= dayRepository.getAllForDay(day);
        DayData root=dayData.get(0);

        float[]weights=new float[dayData.size()];
        weights[0]=1;
        float sigmaWeights=0;

        for(int i=1;i<dayData.size();++i){
            weights[i]=root.getRelevance(dayData.get(i));
            sigmaWeights+=weights[i];
        }

        List<BlockfabrikDatapoint>fiveMinAvg=new ArrayList<>();

        for(int i=0;i<root.getDatapoints().size()-1;++i){
            BlockfabrikDatapoint rootDatapoint= (BlockfabrikDatapoint) root.getDatapoints().toArray()[i];
            BlockfabrikDatapoint dummyDatapoint=new BlockfabrikDatapoint();
            dummyDatapoint.setHour(rootDatapoint.getHour());
            dummyDatapoint.setMinute(rootDatapoint.getMinute());

            float weightedValues=rootDatapoint.getValue();
            for(int j=1;j<dayData.size()-1;++j){
                if(dayData.get(j).getDatapoints().size()<=i)continue;
                BlockfabrikDatapoint tmp= (BlockfabrikDatapoint) dayData.get(j).getDatapoints().toArray()[i];
                weightedValues+=tmp.getValue()*weights[j];
            }
            dummyDatapoint.setValue(Math.round(weightedValues/sigmaWeights));
            fiveMinAvg.add(dummyDatapoint);
        }

        return DatapointUtils.averageDayValues(fiveMinAvg,20);
    }

    public List<DayData> getAllDayData(){
        return dayRepository.getAllDesc();
    }

    public List<BlockfabrikDatapoint> getValuesForDay(Date date, int hour,int minute){
        return datapointRepository.getBeforeTime(date,hour,minute);
    }
}
